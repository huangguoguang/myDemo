package com.tarena.trade.listener;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tarena.entity.UserProfitLoss;
import com.tarena.entity.UserWallet;
import com.tarena.exception.BusinessException;
import com.tarena.lock.DistributeLock;
import com.tarena.mongo.service.MongodbBaseDao;
import com.tarena.service.IProcessOrderService;
import com.tarena.trade.entity.BinaryOptionsSellOrder;
import com.tarena.trade.entity.HangingDelistedSellOrder;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.entity.TradeSellOrderMongodb;
import com.tarena.trade.enums.EnumsTradeDirection;
import com.tarena.trade.utils.IdentificationUtil;
import com.tarena.trade.utils.ProfitLossUtil;
import com.tarena.trade.utils.TradeUtil;
import com.tarena.util.NumberUtil;

@Service
public class TradeProcess {
  private static Logger logger = Logger.getLogger(TradeProcess.class);

  @Autowired
  private MongodbBaseDao<Object> mongodbBaseDao;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private DistributeLock distributeLock;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private AmqpTemplate amqpTemplate;
  /**
   * 建仓确认
   * @param buyOrder
   * @param profitLoss
   * @return
   */
  public boolean buyConfirm(TradeBuyOrderMongodb buyOrder,UserProfitLoss profitLoss){
    //设置订单有效
    mongodbBaseDao.updateFirstById(buyOrder.getOrder_id(),Update.update("confirm", "1"),TradeUtil.Mongodb_Table_TradeBuyOrder);
   //插入流水
    mongodbBaseDao.save(profitLoss, TradeUtil.Mongodb_Table_UserProfitLoss);
    if(TradeUtil.Rule_HangingDelisted.equals(buyOrder.getTrading_rule()) || 
        TradeUtil.Rule_PointOptions.equals(buyOrder.getTrading_rule())){
      //插止盈止损
      mongodbBaseDao.save(TradeUtil.convertBuyOrderForStopLoss(buyOrder), TradeUtil.Mongodb_Table_StopProfitStopLoss);
    }
    return true;
  }
  
  /**
   * 建仓回滚
   * @param buyOrder
   * @param processOrderService
   * @return
   */
  public boolean buyRollBack(TradeBuyOrderMongodb buyOrder,
      IProcessOrderService processOrderService) {
    //删除建仓订单
    mongodbBaseDao.removeById(buyOrder.getOrder_id(),TradeUtil.Mongodb_Table_TradeBuyOrder);
   
    //退回订单金额 
    double orderAmount = buyOrder.getBuy_all_price();
    if(orderAmount > 0){
      String lockKeyActivityLuckydrawOfWallet = distributeLock.getLock("wallet", buyOrder.getUser_id());
      if(lockKeyActivityLuckydrawOfWallet == null){
        return false;
      }
      try{
        String key = "wallet_"+buyOrder.getUser_id();
        double userWallet = Double.valueOf(stringRedisTemplate.opsForValue().get(key));//用户钱包
        double newWallet = userWallet + orderAmount;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(newWallet));
      }finally{
        distributeLock.releaseLock("wallet",buyOrder.getUser_id(), lockKeyActivityLuckydrawOfWallet);
      }
    }
    
    TradeSellOrderMongodb sellOrder = TradeUtil.convertBuyOrderMongodb(buyOrder);
    sellOrder.setBad_type("01");//坏单类型
    sellOrder.setBad_status("1");
    //写坏单mongodb
    mongodbBaseDao.save(sellOrder, TradeUtil.Mongodb_Table_Bad_Order);
    //坏单插入mysql
    processOrderService.insertBadBuyOrder(buyOrder);
    return true;
  }

  public void preProcessSellOrder(TradeSellOrderMongodb sellOrder,
      IProcessOrderService processOrderService) {
    if(checkConfirmdeOrder(sellOrder.getOrder_id())){
      if(TradeUtil.Rule_BinaryOptions.equals(sellOrder.getTrading_rule())){
        //非回滚订单需要设置点位
        if(!"2".equals(sellOrder.getRollback())){
          String text = stringRedisTemplate.opsForValue().get(sellOrder.getBuy_itemtype());
          double sellPoint = Double.parseDouble(JSONObject.parseObject(text).get("ws_new_point").toString());
          
          //平仓点位
          sellOrder.setSell_point(sellPoint);
          //平仓时间
          sellOrder.setSell_time(new Date());
        }
        //固定收益平仓
        processBinaryOptionsSell(sellOrder,processOrderService);
      }else if (TradeUtil.Rule_HangingDelisted.equals(sellOrder.getTrading_rule())) {
        // 挂摘牌平仓
        processHangingDelistedSell(sellOrder, processOrderService);
      } else{
        logger.error("--不支持的交易模式!--");
      }
    }
  }
  /**
   * 挂摘牌平仓
   * 
   * 
   */
  public void processHangingDelistedSell(TradeSellOrderMongodb sellOrder, IProcessOrderService processOrderService) {
    String buyType = sellOrder.getBuy_type(); // 方向
    BigDecimal buyPoint = new BigDecimal(Double.toString(sellOrder.getBuy_point())); // 建仓点位
    BigDecimal sellPoint = new BigDecimal(Double.toString(sellOrder.getSell_point())); // 平仓点位

    BigDecimal price = new BigDecimal(Double.toString(sellOrder.getBuy_price())); // 单笔本金
    BigDecimal lot = new BigDecimal(sellOrder.getBuy_amount()); // 手数
    BigDecimal profit = new BigDecimal(Double.toString(sellOrder.getGzp_profit())); // 波动盈亏
    double principal = price.multiply(lot).doubleValue(); // 本金
    
    Double share = (double) 0; // 盈亏
    if (EnumsTradeDirection.BUY.getCode().equals(buyType)) { // 买涨
      share = sellPoint.subtract(buyPoint).multiply(profit).multiply(lot).doubleValue();
    } else { // 买跌
      share = sellPoint.subtract(buyPoint).multiply(profit).multiply(lot).doubleValue() * -1;
    }

    share = NumberUtil.ROUND_DOWN(2, share); // 保留两位小数

    double settlementValue = principal + share; // 结算 = 本金 + 盈亏

    HangingDelistedSellOrder hdSellOrder = TradeUtil.convertSellOrderForHangingDelisted(sellOrder);

    // 爆仓
    if (settlementValue < 0) {
      share = -principal; // 负本金
      settlementValue = 0; // 结算金额
    }

    // 平仓对象
    hdSellOrder.setGzp_sell_profit_loss(share);
    hdSellOrder.setGzp_settlement_value(settlementValue);
    hdSellOrder.setRule("01"); // 01:挂摘牌 02:固定收益 03:点位期权

    // 取用户余额
    UserWallet userWallet = processOrderService.queryUserWalletById(sellOrder.getUser_id());
    userWallet.setOrder_money(settlementValue); // 订单金额
    userWallet.setType("add"); // 加

    // 流水记录
    UserProfitLoss userProfitLoss = new UserProfitLoss(IdentificationUtil.generateId());
    userProfitLoss.setMoney_type(ProfitLossUtil.ProfitLoss_HangingDelisted_Sell); // 固定收益平仓
    userProfitLoss.setUser_id(hdSellOrder.getUser_id()); // 用户ID
    userProfitLoss.setOrder_id(hdSellOrder.getGzp_order_id()); // 订单ID
    userProfitLoss.setUser_money_before(userWallet.getUser_money()); // 交易前余额
    userProfitLoss.setUser_money(userWallet.getOrder_money()); // 订单金额
    userProfitLoss.setUser_money_after(userWallet.getUser_money() + settlementValue); // 交易后余额
    userProfitLoss.setIs_use("1"); // 是否可用
    userProfitLoss.setCreate_date(new Date()); // 创建时间

    // 平仓反馈
    boolean buyResult = false;
    try {
      // 平仓反馈
      buyResult = processOrderService.processSellOrder(TradeUtil.Rule_HangingDelisted, hdSellOrder,
          userProfitLoss, userWallet);

    } catch (BusinessException e) {
      logger.error("--挂摘牌平仓失败--");
    } catch (Exception e) {
      logger.error("处理挂摘牌平仓订单出错！");
    }

    if (buyResult) {
      // MongoDB对象
      sellOrder.setSell_profit_loss(hdSellOrder.getGzp_sell_profit_loss()); // 盈亏
      sellOrder.setSettlement_value(hdSellOrder.getGzp_settlement_value()); // 结算金额
      processOrderService.sellConfirm(sellOrder, userProfitLoss);
    } else {
      processOrderService.sellRollBack(sellOrder);
    }
  }


  /**
   * 固定收益平仓
   * @param sellOrder
   * @param processOrderService
   */
  private void processBinaryOptionsSell(TradeSellOrderMongodb sellOrder,
      IProcessOrderService processOrderService) {
    String buyType = sellOrder.getBuy_type();//购买方向
    double buyPoint = sellOrder.getBuy_point();//买入点位
    double sellPoint = sellOrder.getSell_point();//卖出点位
    
    double price = sellOrder.getBuy_price();//单笔本金
    double lot = sellOrder.getBuy_amount();//手数
    double principal = price * lot ;//本金
    double ratio = sellOrder.getGdsy_buy_ratio();//比率
    double gapPoint = buyPoint - sellPoint;
    
    double share = 0;//最终金额
    String outType = buyType ;//平仓类型
    if(EnumsTradeDirection.BUY.getCode().equals(buyType)){
      // 买涨
      if(gapPoint < 0) {
        share = principal * ratio /100;
        outType = outType + "profit";//涨盈
      } else if(gapPoint == 0){
        share = 0;
        outType = outType + "equal";//涨平
      } else {
        share = principal * ratio / 100 * -1;
        outType = outType + "loss";//涨损
      }
    } else {
      if(gapPoint > 0) {
        share = principal * ratio /100;
        outType = outType + "profit";//跌盈
      } else if(gapPoint == 0) {
        share = 0;
        outType = outType + "equal";//跌平
      } else {
        share = principal * ratio / 100 * -1;
        outType = outType + "loss";//跌损
      }
    }
    share = NumberUtil.ROUND_DOWN(2, share);//保留两位小数
    
    double settleValue = principal + share;
    
    sellOrder.setSell_type(outType);//平仓类型
    
    BinaryOptionsSellOrder boSellOrder = TradeUtil.convertSellOrderForBinaryOptions(sellOrder);
    //平仓对象
    boSellOrder.setGdsy_sell_profit_loss(share);//盈亏
    boSellOrder.setGdsy_settlement_value(settleValue);//结算金额
    boSellOrder.setRule(TradeUtil.Rule_BinaryOptions);//固定收益平仓
    
    //取用户余额
    UserWallet userWallet = processOrderService.queryUserWalletById(sellOrder.getUser_id());
    userWallet.setOrder_money(settleValue);//平仓订单金额
    userWallet.setType(ProfitLossUtil.ADD);//加
    
    //流水记录
    UserProfitLoss userProfitLoss = new UserProfitLoss(IdentificationUtil.generateId());
    userProfitLoss.setCreate_date(new Date());//流水创建时间
    userProfitLoss.setIs_use("1");
    userProfitLoss.setMoney_type(ProfitLossUtil.ProfitLoss_BinaryOptions_Sell);//固定收益平仓
    userProfitLoss.setOrder_id(boSellOrder.getGdsy_order_id());
    userProfitLoss.setUser_id(boSellOrder.getUser_id());
    userProfitLoss.setUser_money_before(userWallet.getUser_money());//交易前余额
    userProfitLoss.setUser_money(userWallet.getOrder_money());//订单金额
    userProfitLoss.setUser_money_after(userWallet.getUser_money() + settleValue);//交易后余额
    
    //平仓反馈
    boolean buyResult = false;
    try {
      buyResult = processOrderService.processSellOrder(TradeUtil.Rule_BinaryOptions,boSellOrder,userProfitLoss,userWallet);
    } catch (BusinessException e) {
      logger.error("--固定收益平仓失败--",e);
    } catch (Exception e) {
      logger.error("处理固定收益平仓订单出错！");
    }
    
    if(buyResult) {
      sellOrder.setSell_profit_loss(share);//盈亏
      sellOrder.setSettlement_value(settleValue);//结算金额
      
      processOrderService.sellConfirm(sellOrder,userProfitLoss);
    } else {
      processOrderService.sellRollBack(sellOrder);
    }
  }

  /**
   * 验证订单是否有效
   * @param order_id
   * @return
   */
  private boolean checkConfirmdeOrder(String order_id) {
    TradeBuyOrderMongodb buyOrder = mongoTemplate.findOne(Query.query(Criteria.where("order_id").is(order_id)),
        TradeBuyOrderMongodb.class, TradeUtil.Mongodb_Table_TradeBuyOrder);
    if(buyOrder == null) {
      return false;
    }else{
      String confirm = buyOrder.getConfirm();
      if(!confirm.equals("1")){
        return false;
      }
    }
    return true;
  }

  /**
   * 平仓确认
   * @param sellOrder
   * @param userProfitLoss
   * @return
   */
  public boolean sellConfirm(TradeSellOrderMongodb sellOrder,
      UserProfitLoss userProfitLoss) {
    //删除建仓订单
    mongodbBaseDao.removeById(sellOrder.getOrder_id(), TradeUtil.Mongodb_Table_TradeBuyOrder);
    
    //插入平仓订单
    mongodbBaseDao.save(sellOrder, TradeUtil.Mongodb_Table_TradeSellOrder);
    //插入流水
    mongodbBaseDao.save(userProfitLoss, TradeUtil.Mongodb_Table_UserProfitLoss);
    
    if(TradeUtil.Rule_HangingDelisted.equals(sellOrder.getTrading_rule())
        || TradeUtil.Rule_PointOptions.equals(sellOrder.getTrading_rule())){
      //删止盈止损
      mongodbBaseDao.removeById(sellOrder.getOrder_id(), TradeUtil.Mongodb_Table_StopProfitStopLoss);
    }
    //加结算金额
    double orderAmount = sellOrder.getSettlement_value();
    double sellProfitLoss = sellOrder.getSell_profit_loss();//盈亏金额
    if(orderAmount > 0) {
      if(TradeUtil.Rule_Crowdfunding.equals(sellOrder.getTrading_rule()) && sellProfitLoss > 0){
        //众筹盈利大于0，生成 gzp订单
        TradeBuyOrderMongodb step2BuyOrder = TradeUtil.convertSellOrderForCrowdfunding(sellOrder);
        mongodbBaseDao.save(step2BuyOrder, TradeUtil.Mongodb_Table_TradeBuyOrder);
     // 建仓订单写MQ
        amqpTemplate.convertAndSend("wp.tarena.trade.in", "wp.rk.trade.hd.buy", 
            JSONObject.toJSON(step2BuyOrder));
        return true;
      }
      
      String lockKeyActivityLuckyDrawOfWallet = distributeLock.getLock("wallet", sellOrder.getUser_id());
      if(lockKeyActivityLuckyDrawOfWallet == null){
        return false;
      }
      try{
        String redisKey = "wallet_"+sellOrder.getUser_id();
        double userWallet = Double.parseDouble(stringRedisTemplate.opsForValue().get(redisKey));
        double newWallet = userWallet + orderAmount;
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(newWallet));
      }finally{
        distributeLock.releaseLock("wallet", sellOrder.getUser_id(), lockKeyActivityLuckyDrawOfWallet);
      }
      
    }
    return true;
  }

  /**
   * 平仓回滚
   * @param sellOrder
   * @param processOrderService
   * @return
   */
  public boolean sellRollBack(TradeSellOrderMongodb sellOrder,
      IProcessOrderService processOrderService) {
    if("2".equals(sellOrder.getRollback())){
      if(TradeUtil.Rule_HangingDelisted.equals(sellOrder.getTrading_rule())){
        //删止盈止损
        mongodbBaseDao.removeById(sellOrder.getOrder_id(), TradeUtil.Mongodb_Table_StopProfitStopLoss);
      }
      //写坏单表
      logger.error("坏单:" + sellOrder);
      //删建仓mongodb
      mongodbBaseDao.removeById(sellOrder.getOrder_id(), TradeUtil.Mongodb_Table_TradeBuyOrder);
      sellOrder.setBad_type("02");
      sellOrder.setBad_status("0");
      //写坏单mongodb
      mongodbBaseDao.save(sellOrder, TradeUtil.Mongodb_Table_Bad_Order);
      //mysql
      processOrderService.insertBadSellOrder(sellOrder);
    } else {
      sellOrder.setRollback("2");//平仓回滚
      amqpTemplate.convertAndSend("wp.tarena.trade.out", "wp.rk.trade.back.sell", JSONObject.toJSON(sellOrder));
    } 
    return true;
  }
}

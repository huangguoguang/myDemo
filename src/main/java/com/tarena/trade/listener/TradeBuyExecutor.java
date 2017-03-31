package com.tarena.trade.listener;

import java.util.Date;

import org.apache.log4j.Logger;

import com.tarena.entity.UserProfitLoss;
import com.tarena.entity.UserWallet;
import com.tarena.exception.BusinessException;
import com.tarena.service.IProcessOrderService;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.utils.IdentificationUtil;
import com.tarena.trade.utils.ProfitLossUtil;
import com.tarena.trade.utils.TradeUtil;

public class TradeBuyExecutor implements Runnable {
  private static Logger logger = Logger.getLogger(TradeBuyExecutor.class);
  private IProcessOrderService processOrderService;
  
  private TradeBuyOrderMongodb buyOrder;

  public TradeBuyExecutor(IProcessOrderService processOrderService,
      TradeBuyOrderMongodb buyOrder) {
    this.processOrderService = processOrderService;
    this.buyOrder = buyOrder;
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    
    String tradingRules = buyOrder.getTrading_rule();
    if(tradingRules.equals(TradeUtil.Rule_HangingDelisted)){
      processHangingDelistedOrder();
    }else if(tradingRules.equals(TradeUtil.Rule_BinaryOptions)){
      processBinaryOptionsOrder();
    }else if(tradingRules.equals(TradeUtil.Rule_PointOptions)){
      processHangingDelistedOrder();
    }else if(tradingRules.equals(TradeUtil.Rule_Crowdfunding) && buyOrder.getStep().equals("1")){
      processBinaryOptionsOrder();
    }else{
      logger.error("不支持的交易模式");
    }
    
    long end = System.currentTimeMillis();
    if(logger.isInfoEnabled()){
      long time = end - start;
      System.out.println("建仓MQ消费所需时间："+time);
    }
  }

  /**
   * 固定收益建仓
   */
  private void processBinaryOptionsOrder() {
    //取用户余额
    UserWallet userWallet = processOrderService.queryUserWalletById(buyOrder.getUser_id());
    userWallet.setOrder_money(buyOrder.getBuy_all_price());//订单金额
    userWallet.setType(ProfitLossUtil.SUBTRACT);//减
    //生成流水记录
    UserProfitLoss userProfitLoss = new UserProfitLoss(IdentificationUtil.generateId());
    //流水
    userProfitLoss.setMoney_type(ProfitLossUtil.ProfitLoss_BinaryOptions_Buy);//固定收益买
    userProfitLoss.setUser_id(buyOrder.getUser_id());
    userProfitLoss.setOrder_id(buyOrder.getOrder_id());
    userProfitLoss.setIs_use("1");
    userProfitLoss.setUser_money_before(userWallet.getUser_money());
    userProfitLoss.setUser_money(userWallet.getOrder_money());
    userProfitLoss.setUser_money_after(userWallet.getUser_money() -  userWallet.getOrder_money());
    userProfitLoss.setCreate_date(new Date());//流水创建时间
    boolean buyResult = false;//建仓反馈
    try {
      buyResult = processOrderService.processBuyOrder(buyOrder.getTrading_rule(), TradeUtil.convertBuyOrderForBinaryOptions(buyOrder), userProfitLoss, userWallet);
    } catch (BusinessException e) {
      logger.error("建仓失败");
      e.printStackTrace();
    }
    if(buyResult){
      processOrderService.buyConfirm(buyOrder, userProfitLoss);
    }else{
      processOrderService.buyRollBack(buyOrder);
    }
  }

  /**
   * 挂摘牌建仓
   */
  private void processHangingDelistedOrder() {
    //取用户余额
    UserWallet userWallet = processOrderService.queryUserWalletById(buyOrder.getUser_id());
    userWallet.setOrder_money(buyOrder.getBuy_all_price());//订单金额
    userWallet.setType(ProfitLossUtil.SUBTRACT);//减
    
    //生成流水记录
    UserProfitLoss userProfitLoss = new UserProfitLoss(IdentificationUtil.generateId());
    //流水
    userProfitLoss.setMoney_type(ProfitLossUtil.ProfitLoss_HangingDelisted_Buy);//挂摘牌买
    userProfitLoss.setUser_id(buyOrder.getUser_id());
    userProfitLoss.setOrder_id(buyOrder.getOrder_id());
    userProfitLoss.setUser_money_before(userWallet.getUser_money());//交易前金额
    userProfitLoss.setUser_money(userWallet.getOrder_money());//交易订单金额
    userProfitLoss.setUser_money_after(userWallet.getUser_money() - userWallet.getOrder_money());//交易后余额
    userProfitLoss.setIs_use("1");//是否可用
    userProfitLoss.setCreate_date(new Date());//流水创建时间
    
    //建仓反馈  成功或失败
    boolean buyResult = false;
    try {
      buyResult = processOrderService.processBuyOrder(buyOrder.getTrading_rule(),TradeUtil.convertBuyOrderForHangingDelisted(buyOrder),userProfitLoss,userWallet);
    } catch (BusinessException e) {
      logger.error("建仓失败");
      e.printStackTrace();
    }
    if(buyResult){
      //成功将confirm更新为1
      processOrderService.buyConfirm(buyOrder,userProfitLoss);
    }else{
      //失败更新为0
      processOrderService.buyRollBack(buyOrder);
    }
    
  }

}

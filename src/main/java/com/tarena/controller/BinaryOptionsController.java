package com.tarena.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tarena.common.component.QuotationComponent;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.common.component.session.support.Session;
import com.tarena.entity.AggregationContent;
import com.tarena.entity.UserInfo;
import com.tarena.exception.ValidationFailureException;
import com.tarena.lock.DistributeLock;
import com.tarena.service.IBinaryOptionsService;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.enums.EnumsTradeDirection;
import com.tarena.trade.utils.DateFormatUtil;
import com.tarena.trade.utils.IdentificationUtil;
import com.tarena.trade.utils.TradeUtil;

/**
 * @author Administrator
 *二元期权（固定收益）
 */
@Controller
@RequestMapping("/binaryOptions")
public class BinaryOptionsController extends BaseController{
  private Logger logger = Logger.getLogger(BinaryOptionsController.class);

  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private DistributeLock distributeLock;
  
  @Autowired
  private IBinaryOptionsService binaryOptionsService;
  
  @Autowired
  private QuotationComponent quotationCom;
  /**
   * 固定收益，建仓
   * @param session
   * @param map
   * @return
   */
  @RequestMapping("/buy")
  @ResponseBody
  public Object buy(@Session(create= false) SessionProvider session,@RequestParam Map<String, Object> map){
    //建仓开始
    long start = System.currentTimeMillis();
    
    //取参数
    int buyLot = Integer.parseInt(map.get("lot").toString());
    String buyType = map.get("type").toString();
    String itemId = map.get("id").toString();
    
    if(buyLot <= 0){
      msg.setSuccess(false);
      msg.setMsg("购买手数必须大于0");
      return msg;
    }
    if(!EnumsTradeDirection.container(buyType)){
      msg.setSuccess(false);
      msg.setMsg("交易方向必须为购物或赊货");
      return msg;
    }
    if(session == null){
      msg.setSuccess(false);
      msg.setMsg("请登录后再操作");
      return msg;
    }
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    if(userInfo == null){
      msg.setSuccess(false);
      msg.setMsg("请登录后再操作");
      return msg;
    }
    UserInfo mUserInfo = mongoTemplate.findOne(new Query(Criteria.where("user_id").is(userInfo.getUser_id())), UserInfo.class, TradeUtil.Mongodb_Table_User);
    if(!mUserInfo.getIs_use().equals("1")){
      msg.setSuccess(false);
      msg.setMsg("账户不可用");
      return msg;
    }
    String disableAgents = stringRedisTemplate.opsForValue().get("disable_agent_id");
    String marketAgent = "#" + userInfo.getAgent_id() +"#";
    if(StringUtils.isNoneBlank(disableAgents) && disableAgents.contains(marketAgent)){
      msg.setSuccess(false);
      msg.setMsg("代理商暂停交易");
      return msg;
    }
    
    String itemIdStr = stringRedisTemplate.opsForValue().get("gdsy_"+userInfo.getAgent_id()+"_"+itemId);
    if(StringUtils.isBlank(itemIdStr)){
      msg.setSuccess(false);
      msg.setMsg("交易商品不存在");
      return msg;
    }
    String itemInfoStr = stringRedisTemplate.opsForValue().get("gdsy_goods_"+itemId);
    if(StringUtils.isBlank(itemInfoStr)){
      msg.setSuccess(false);
      msg.setMsg("交易商品不存在");
      return msg;
    }
    
    JSONObject json = JSONObject.parseObject(itemInfoStr);
    String is_use = json.getString("is_use");
    if(!is_use.equals("1")){
      msg.setSuccess(false);
      msg.setMsg("交易商品不可用");
      return msg;
    }
    
    double gdsy_ratio = Double.parseDouble(json.getString("gdsy_ratio"));//比率     建仓盈亏率
    double gdsy_poundage = Double.parseDouble(json.getString("gdsy_poundage"));//手续费
    double gdsy_money = Double.parseDouble(json.getString("gdsy_money"));//金额
    int maxLot = Integer.valueOf(json.getString("gdsy_lot"));//最大购买手数
    int gdsy_time = Integer.valueOf(json.getString("gdsy_time"));//delay时间    多少秒   页面显示 购买类型   60/60 = 1M   小于60显示秒
    
    String buy_number = json.getString("gdsy_number");//商品数型   商品号
    String gdsy_unit = json.getString("gdsy_unit");//交易单位
    String gdsy_alias = json.getString("gdsy_alias");//别名
    String gdsy_type = json.getString("gdsy_type");//商品数型   建仓数型
    String gdsy_name = json.getString("gdsy_name");//商品名称
    String gdsy_code = json.getString("gdsy_code");//建仓品种   1件，5件
    String buy_date = json.getString("gdsy_buy_no_date");//允许交易日期
    String buy_time = json.getString("gdsy_buy_no_time");//允许交易时间
    
    //交易时间判断 
    try {
      if(!TradeUtil.checkTradeDateAddSecond(buy_date, buy_time, gdsy_time)){
        msg.setSuccess(false);
        msg.setMsg("非交易时间，无法交易");
        return msg;
      }
    } catch (ValidationFailureException e) {
      msg.setSuccess(false);
      msg.setMsg(e.getMessage());
      return msg;
    }
    //最大手数判断 
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("user_id").is(userInfo.getUser_id()).and("trading_rule").is(TradeUtil.Rule_BinaryOptions)
            .and("buy_itemcode").is(gdsy_code).and("buy_type").is(buyType).and("confirm").ne("0")),
        Aggregation.group("buy_itemcode").sum("buy_amount").as("holder_lot"));
    AggregationResults<AggregationContent> resultLot = mongoTemplate.aggregate(agg, 
        TradeUtil.Mongodb_Table_TradeBuyOrder, AggregationContent.class);
    List<AggregationContent> list = resultLot.getMappedResults();
    long holderLot = 0;
    if(list.size() > 0){
      holderLot = list.get(0).getHolder_lot();
    }
    if(holderLot + buyLot > maxLot) {
      msg.setSuccess(false);
      msg.setMsg("当前商品，购买数量不能大于"+maxLot + "笔");//"当前商品，建仓手数不能大于"
      return msg;
    }
    //购买金额判断
    double totalPrice = gdsy_money * buyLot;
    double totalPoundage = gdsy_poundage * buyLot;
    double orderAmount = totalPrice + totalPoundage;
    
    //获取行情数据
    double buyPoint = quotationCom.getBuyPoint(gdsy_type, buyType);
    //更新用户钱包
    //先加钱包锁
    String lockKeyActivityLuckydrawOfWallet = distributeLock.getLock("wallet", userInfo.getUser_id());
    if(lockKeyActivityLuckydrawOfWallet == null) {
      msg.setSuccess(false);
      msg.setMsg("取用户金额锁失败");
      return msg;
    }
    
    try{
      String redisKey = "wallet_"+userInfo.getUser_id();
      String walletText = stringRedisTemplate.opsForValue().get(redisKey);
      if(walletText == null) {
        msg.setSuccess(false);
        msg.setMsg("取用户钱包失败");
        return msg;
      }
      double userWallet = Double.parseDouble(walletText);
      if(userWallet > 0 && userWallet > orderAmount){
        double newWallet = userWallet - orderAmount;
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(newWallet));
      } else {
        msg.setSuccess(false);
        msg.setMsg("您的余额不足。");
        msg.setCode("000001");
        return msg;
      }
    }finally{
      distributeLock.releaseLock("wallet", userInfo.getUser_id(), lockKeyActivityLuckydrawOfWallet);
    }
    
    //准备建仓订单
    TradeBuyOrderMongodb buyOrder = new TradeBuyOrderMongodb();
    String oid = IdentificationUtil.generateId();
    buyOrder.setAgent_id(userInfo.getAgent_id());
    buyOrder.setOrder_id(oid);
    buyOrder.setP_id(userInfo.getP_id());
    buyOrder.setCh_id(userInfo.getCh_id());
    buyOrder.setCe_id(userInfo.getCe_id());
    buyOrder.setSettle_id(userInfo.getSettle_id());
    buyOrder.setSettle_name(userInfo.getSettle_name());
    buyOrder.setAgent_name(userInfo.getAgent_name());
    buyOrder.setDept_id(userInfo.getDept_id());
    buyOrder.setDept_name(userInfo.getDept_name());
    buyOrder.setBroker_id(userInfo.getBroker_id());
    buyOrder.setBroker_name(userInfo.getBroker_name());
    buyOrder.setUser_id(userInfo.getUser_id());
    buyOrder.setUser_name(userInfo.getUser_name());
    buyOrder.setUser_mobile(userInfo.getUser_demobile());
    
    Date date = new Date();
    buyOrder.setBuy_type(buyType);
    buyOrder.setBuy_point(buyPoint);
    buyOrder.setBuy_unit(gdsy_unit);
    buyOrder.setBuy_time(date);
    buyOrder.setSell_time(DateFormatUtil.addSecond(date, gdsy_time));//平仓时间
    buyOrder.setBuy_itemcode(gdsy_code);//商品代码品种
    buyOrder.setBuy_itemtype(gdsy_type);//商品数型
    buyOrder.setBuy_itemname(gdsy_name);
    buyOrder.setBuy_price(gdsy_money);//单价
    buyOrder.setBuy_amount(buyLot);//购买数量
    buyOrder.setBuy_brokerage(gdsy_poundage);//手续费
    buyOrder.setBuy_all_price(orderAmount);//总金额
    buyOrder.setBuy_number(buy_number);//商品编号
    buyOrder.setBuy_itemname_alias(gdsy_alias);//别名 
    buyOrder.setGdsy_buy_ratio(gdsy_ratio);//比率
    buyOrder.setGdsy_buy_xz_time(gdsy_time);//延期时间
    
    buyOrder.setTrading_rule(TradeUtil.Rule_BinaryOptions);//交易类型 固定收益
    
    buyOrder.setItem_id(itemId);//商品ID
    buyOrder.setIs_use("1");
    buyOrder.setConfirm("2");//建仓中
    
    long end = System.currentTimeMillis();//初步建仓完成时间
    boolean rst = false;
    try {
      rst = binaryOptionsService.buy(buyOrder);
    } catch (Exception e) {
      logger.error("写建仓队列异常", e);
    }
    if(rst){
      msg.setSuccess(true);
      msg.setMsg("众筹购买已受理");//建仓已受理
    }else{
      msg.setSuccess(false);
      msg.setMsg("众筹购买失效");//建仓失败
    }
    if(logger.isInfoEnabled()){
      long time = end - start ;
      System.out.println("固定收益WEB建仓所需时间：" + time + " 订单号：" + oid);
    }
    return msg;
  }
}

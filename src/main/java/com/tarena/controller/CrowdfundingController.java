package com.tarena.controller;

import java.util.Date;
import java.util.HashMap;
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
import com.tarena.lock.DistributeLock;
import com.tarena.service.IBinaryOptionsService;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.enums.EnumsTradeDirection;
import com.tarena.trade.utils.IdentificationUtil;
import com.tarena.trade.utils.TradeUtil;

@Controller
@RequestMapping("/crowdfunding")
public class CrowdfundingController extends BaseController{
  private static Logger logger = Logger.getLogger(CrowdfundingController.class);
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private QuotationComponent quotation;
  
  @Autowired
  private DistributeLock distributeLock;
  
  @Autowired
  private IBinaryOptionsService binaryOptionsService;
  
  @RequestMapping("/buy")
  @ResponseBody
  public Object buy(@Session(create = false)SessionProvider session,@RequestParam Map<String,Object> map){
    //判断是否在交易时间
    String cfType = stringRedisTemplate.opsForValue().get("crowdfundingBuyType");
    if(StringUtils.isBlank(cfType) || StringUtils.equals("0", cfType)){
      msg.setSuccess(false);
      msg.setMsg("当前时间不允许购买");
      return msg;
    }
    
    //建仓开始
    long startTime = System.currentTimeMillis();
    
    String buyType = map.get("type").toString();
    int buyLot = Integer.parseInt(map.get("lot").toString());
    String itemId = map.get("id").toString();
    
    if(buyLot <= 0) {
      msg.setSuccess(false);
      msg.setMsg("购买手数必须大于0");
      return msg;
    }
    
    if(!EnumsTradeDirection.container(buyType)){
      msg.setSuccess(false);
      msg.setMsg("交易类型必须为购物或赊货");
      return msg;
    }
    
    if(session == null){
      msg.setSuccess(false);
      msg.setMsg("请登录后再操作");
      return msg;
    }
    UserInfo userInfo = (UserInfo)session.getAttribute(UserInfo.sessionKey);
    if(userInfo == null){
      msg.setSuccess(false);
      msg.setMsg("请登录后再操作");
      return msg;
    }
    
    UserInfo mUserInfo = mongoTemplate.findOne(new Query(Criteria.where("user_id").is(userInfo.getUser_id())), UserInfo.class,TradeUtil.Mongodb_Table_User);
    if(!StringUtils.equals("1", mUserInfo.getIs_use())){
      msg.setSuccess(false);
      msg.setMsg("您的账户不可用");
      return msg;
    }
    
    String disableAgentId = stringRedisTemplate.opsForValue().get("disable_agent_id");
    if(StringUtils.isNotBlank(disableAgentId) && disableAgentId.contains("#" + mUserInfo.getAgent_id() + "#")){
      msg.setSuccess(false);
      msg.setMsg("代理商暂停交易");
      return msg;
    }
    
    String itemIdStr = stringRedisTemplate.opsForValue().get("gzp_"+ mUserInfo.getAgent_id() + "_" + itemId);
    if(StringUtils.isBlank(itemIdStr)){
      msg.setSuccess(false);
      msg.setMsg("交易商品不存在 ");
      return msg;
    }
      
    String itemStr = stringRedisTemplate.opsForValue().get("gzp_goods_" + itemId);
    if(StringUtils.isBlank(itemStr)){
      msg.setSuccess(false);
      msg.setMsg("交易商品不存在");
      return msg;
    }
    
    JSONObject json = JSONObject.parseObject(itemStr);
    if(!StringUtils.equals("1", json.get("is_use").toString())){
      msg.setSuccess(false);
      msg.setMsg("交易商品不可用");
      return msg;
    }
    
    String buy_number = json.get("gzp_number").toString();
    String gdsy_type = json.get("gzp_type").toString();
    String gdsy_code = json.get("gzp_code").toString();
    String buy_unit = json.get("gzp_unit").toString();
    String alias = json.get("gzp_alias").toString();
    String gdsy_name = json.get("gzp_name").toString();
    
    double poundage = Double.parseDouble(json.get("gzp_poundage").toString());
    double profit = Double.parseDouble(json.get("gzp_profit").toString());
    double gdsy_money = Double.parseDouble(json.get("gzp_money").toString());
    
    int maxLot = Integer.parseInt(json.get("gzp_lot").toString());
    
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("user_id").is(userInfo.getUser_id()).and("is_confirm").ne("0").
            and("trading_rule").is(TradeUtil.Rule_Crowdfunding).and("buy_type").is(buyType).and("buy_itemcode").is(gdsy_code)),
        Aggregation.group("buy_itemcode").sum("buy_amount").as("holder_lot"));
    AggregationResults<AggregationContent> result = mongoTemplate.aggregate(agg, TradeUtil.Mongodb_Table_TradeBuyOrder, AggregationContent.class);
    
    List<AggregationContent> list = result.getMappedResults();
    long holderLot = 0;
    if(!list.isEmpty()){
      holderLot = list.get(0).getHolder_lot();
    }
    if(holderLot + buyLot > maxLot){
      msg.setSuccess(false);
      msg.setMsg("当前商品购买数量不能大于 " + maxLot + "笔");
      return msg;
    }
    
    double totalPrice = gdsy_money * buyLot;
    double totalPoundage = poundage * buyLot;
    double orderAmount = totalPoundage + totalPrice;
    
    double buyPoint = quotation.getBuyPoint(gdsy_type, buyType);
    
    String lockKeyActivityWallet = distributeLock.getLock("wallet", userInfo.getUser_id());
    if(lockKeyActivityWallet == null) {
      msg.setSuccess(false);
      msg.setMsg("取用户金额锁失败");
      return msg;
    }
    try{
      String redisKey = "wallet_"+userInfo.getUser_id();
      String walletText = stringRedisTemplate.opsForValue().get(redisKey);//用户钱包
      if(walletText == null){
        msg.setSuccess(false);
        msg.setMsg("您的余额获取失败");
        return msg;
      }
      double userWallet = Double.parseDouble(walletText);
      if(userWallet > 0 && userWallet >= orderAmount) {
        double newWallet = userWallet - orderAmount;
        //更新redis
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(newWallet));
      } else {
        msg.setSuccess(false);
        msg.setMsg("您的余额不足");
        msg.setCode("000001");
        return msg;
      }
    }finally{
      distributeLock.releaseLock("wallet", userInfo.getUser_id(),lockKeyActivityWallet);
    }
    
    //准备订单信息
    TradeBuyOrderMongodb buyOrder = new TradeBuyOrderMongodb();
    String oid = IdentificationUtil.generateId();
    buyOrder.setOrder_id(oid);
    buyOrder.setAgent_id(mUserInfo.getAgent_id());
    buyOrder.setAgent_name(userInfo.getAgent_name());
    buyOrder.setP_id(userInfo.getP_id());
    buyOrder.setCh_id(userInfo.getCh_id());
    buyOrder.setCe_id(userInfo.getCe_id());
    buyOrder.setSettle_id(userInfo.getSettle_id());
    buyOrder.setSettle_name(userInfo.getSettle_name());
    buyOrder.setDept_id(userInfo.getDept_id());
    buyOrder.setDept_name(userInfo.getDept_name());
    buyOrder.setBroker_id(userInfo.getBroker_id());
    buyOrder.setBroker_name(userInfo.getBroker_name());
    buyOrder.setUser_id(userInfo.getUser_id());
    buyOrder.setUser_name(userInfo.getUser_name());
    buyOrder.setUser_mobile(userInfo.getUser_mobile());
    
    buyOrder.setBuy_type(buyType);
    buyOrder.setBuy_point(buyPoint);
    buyOrder.setBuy_unit(buy_unit);
    buyOrder.setBuy_time(new Date());
    buyOrder.setBuy_itemtype(gdsy_type);
    buyOrder.setBuy_itemcode(gdsy_code);
    buyOrder.setBuy_itemname(gdsy_name);
    buyOrder.setBuy_price(gdsy_money);
    buyOrder.setBuy_brokerage(totalPoundage);
    buyOrder.setBuy_all_price(totalPrice);
    buyOrder.setBuy_amount(buyLot);
    buyOrder.setBuy_number(buy_number);
    buyOrder.setGzp_profit(profit);
    buyOrder.setItem_id(itemId);
    buyOrder.setIs_use("1");
    buyOrder.setStep("1");// 众筹固定收益
    buyOrder.setTrading_rule(TradeUtil.Rule_Crowdfunding);
    buyOrder.setConfirm("2");
    buyOrder.setBuy_itemname_alias(alias);
    
    long endTime = System.currentTimeMillis();
    boolean rst = false;
    try {
      rst = binaryOptionsService.buy(buyOrder);
    } catch (Exception e) {
      logger.error("写固定收益众筹建仓队列异常", e);
    }
    if(rst){
      msg.setSuccess(true);
      msg.setMsg("众筹购买已受理");//建仓已受理
    }else{
      msg.setSuccess(false);
      msg.setMsg("众筹购买失效");//建仓失败
    }
    if(logger.isInfoEnabled()){
      long time = endTime - startTime;
      System.out.println("挂摘牌WEB建仓所需时间:"+time + "订单号:"+oid);
    }
    
    return msg;
  }
  
  @RequestMapping("/tradeInfo")
  @ResponseBody
  public Object tradeInfo(){
    Map<String,Object> resultmap = new HashMap<String, Object>();
    String buyType = stringRedisTemplate.opsForValue().get("crowdfundingBuyType");//  // 0 不可交易; 1 可交易; 2 收盘
    resultmap.put("buyType", buyType);
    if("0".equals(buyType)){
      String startTime = stringRedisTemplate.opsForValue().get("crowdfundingStartTime");//当不可交易时，先找到可以交易的开始时间
      if(StringUtils.isNoneBlank(startTime)){
        long countdown =  Long.parseLong(startTime) - System.currentTimeMillis();
        if(countdown > 0){
          resultmap.put("countdown", countdown);
         } else{
           // 时间为负或为零时可买
           resultmap.put("buyType", "1");
         }
      }
    }
    String crowdfundingEndTime = stringRedisTemplate.opsForValue().get("crowdfundingEndTime");
    if(StringUtils.isNotBlank(crowdfundingEndTime)){
      long countdown = Long.parseLong(crowdfundingEndTime) - System.currentTimeMillis() ;
      if(countdown > 0){
       resultmap.put("countend", countdown);
      } 
    }
    return resultmap;
  }

}

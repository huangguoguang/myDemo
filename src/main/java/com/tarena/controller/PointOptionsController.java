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
import com.tarena.service.IHangingDelistedService;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.enums.EnumsTradeDirection;
import com.tarena.trade.utils.IdentificationUtil;
import com.tarena.trade.utils.TradeUtil;

/**
 * @author Administrator
 * 点位期权
 */
@Controller
@RequestMapping("/pointOptions")
public class PointOptionsController extends BaseController{
  private Logger logger = Logger.getLogger(PointOptionsController.class);

  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private DistributeLock distributeLock;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private QuotationComponent quotationCom;
  
  @Autowired
  private IHangingDelistedService hangingDelistedService;
  
  @RequestMapping("/buy")
  @ResponseBody
  public Object buy(@Session(create = false) SessionProvider session,@RequestParam Map<String,Object> map){
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
    
    String itemIdStr = stringRedisTemplate.opsForValue().get("dwqq_"+userInfo.getAgent_id()+"_"+itemId);
    if(StringUtils.isBlank(itemIdStr)){
      msg.setSuccess(false);
      msg.setMsg("交易商品不存在");
      return msg;
    }
    String itemInfoStr = stringRedisTemplate.opsForValue().get("dwqq_goods_"+itemId);
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
    String gdsy_name = json.get("gdsy_name").toString(); // 商品名称
    String gdsy_type = json.get("gdsy_type").toString(); // 商品数型
    String gdsy_code = json.get("gdsy_code").toString(); // 商品代码
    String buy_number = json.getString("gdsy_number").toString(); // 商品数型
    String buy_unit = json.get("gdsy_unit").toString(); // 单位
    double gdsy_money = Double.valueOf(json.get("gdsy_money").toString()); // 金额
    double gdsy_poundage = Double.valueOf(json.get("gdsy_poundage").toString()); // 手续费
    double gdsy_ratio = Double.valueOf(json.get("gdsy_ratio").toString()); // 比率
    int maxLot = Integer.valueOf(json.get("gdsy_lot").toString()); // 最大手数
    String buy_date = json.get("gdsy_buy_no_date").toString(); // 日期
    String buy_time = json.get("gdsy_buy_no_time").toString(); // 时间
    String alias = json.get("gdsy_alias").toString(); // 别名
    
    double floatPoint = Double.valueOf(json.get("gdsy_time").toString()); // 点位期权/浮动点位

    // 交易时间判断
    try{
      if (!TradeUtil.checkTradeDate(buy_date, buy_time)) {
        msg.setSuccess(false);
        msg.setMsg("非交易时间，无法交易");
        return msg;
      }
    }catch(ValidationFailureException e){
      msg.setSuccess(false);
      msg.setMsg(e.getMessage());
      return msg;
    }

    // 最大手数判断
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(
            Criteria.where("user_id").is(userInfo.getUser_id()).and("trading_rule").is(TradeUtil.Rule_PointOptions)
                .and("buy_itemcode").is(gdsy_code).and("buy_type").is(buyType).and("confirm").ne("0")),
        Aggregation.group("buy_itemcode").sum("buy_amount").as("holder_lot"));
    AggregationResults<AggregationContent> resultLot = mongoTemplate.aggregate(agg,
        TradeUtil.Mongodb_Table_TradeBuyOrder, AggregationContent.class);

    List<AggregationContent> list = resultLot.getMappedResults();
    long holderLot = 0;
    if (!list.isEmpty()) {
      holderLot = list.get(0).getHolder_lot();
    }
    if (holderLot + buyLot > maxLot) {
      msg.setSuccess(false);
      msg.setMsg("当前商品，购买数量不能大于" + maxLot + "笔!"); // backMsg.setMsg("当前商品，建仓手数不能大于" + maxLot + "手!");
      return msg;
    }
    
    double buyPoint = quotationCom.getBuyPoint(gdsy_type, buyType);
    
    // 金额判断 用户REDIS余额 - 订单金额 > 0
    double totalPrice = gdsy_money * buyLot;
    double totalPoundage = gdsy_poundage * buyLot;
    double orderAmount = totalPrice + totalPoundage;

 // 更新用户余额
    String lockKeyActivityLuckydrawOfWallet = distributeLock.getLock("wallet", userInfo.getUser_id());
    if (lockKeyActivityLuckydrawOfWallet == null) {
      msg.setSuccess(false);
      msg.setMsg("取用户金额锁失败");
      return msg;
    }
    try {
      String redisKey = "wallet_" + userInfo.getUser_id();

      String walletText = stringRedisTemplate.opsForValue().get(redisKey); // 用户钱包
      if (walletText == null) {
        msg.setSuccess(false);
        msg.setMsg("您的余额获取失败!");
        return msg;
      }

      double userWallet = Double.valueOf(walletText);

      if (userWallet > 0 && userWallet >= orderAmount) {
        double newWallet = userWallet - orderAmount;
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(newWallet));
      } else {
        msg.setSuccess(false);
        msg.setMsg("您的余额不足。");
        msg.setCode("000001");
        return msg;
      }
    } finally {
      distributeLock.releaseLock("wallet", userInfo.getUser_id(), lockKeyActivityLuckydrawOfWallet);
    }
    
    //点位期权建仓订单
    String oid = IdentificationUtil.generateId();
    TradeBuyOrderMongodb buyOrder = new TradeBuyOrderMongodb();
    buyOrder.setPoint(floatPoint);
    if(EnumsTradeDirection.BUY.match(buyType)){
      buyOrder.setGzp_sell_zs_point(buyPoint - floatPoint);
      buyOrder.setGzp_sell_zy_point(buyPoint + floatPoint);
    } else {
      buyOrder.setGzp_sell_zs_point(buyPoint + floatPoint);
      buyOrder.setGzp_sell_zy_point(buyPoint - floatPoint);
    }
    buyOrder.setOrder_id(oid); // 建仓ID
    // 层级
    buyOrder.setP_id(userInfo.getP_id());
    buyOrder.setCh_id(userInfo.getCh_id());
    buyOrder.setCe_id(userInfo.getCe_id());
    buyOrder.setSettle_id(userInfo.getSettle_id()); // 结算
    buyOrder.setSettle_name(userInfo.getSettle_name());
    buyOrder.setAgent_id(userInfo.getAgent_id()); // 代理商ID
    buyOrder.setAgent_name(userInfo.getAgent_name());
    buyOrder.setDept_id(userInfo.getDept_id()); // 部门
    buyOrder.setDept_name(userInfo.getDept_name());
    buyOrder.setBroker_id(userInfo.getBroker_id()); // 经纪人ID
    buyOrder.setBroker_name(userInfo.getBroker_name());
    buyOrder.setUser_id(userInfo.getUser_id()); // 用户ID
    buyOrder.setUser_name(userInfo.getUser_name());
    buyOrder.setUser_mobile(userInfo.getUser_demobile()); // 手机号

    buyOrder.setBuy_type(buyType); // 方向
    buyOrder.setBuy_point(buyPoint); // 建仓点位
    buyOrder.setBuy_time(new Date()); // 建仓时间
    buyOrder.setBuy_itemtype(gdsy_type); // 商品数型
    buyOrder.setBuy_itemcode(gdsy_code); // 商品CODE
    buyOrder.setBuy_itemname(gdsy_name); // 商品名称
    buyOrder.setBuy_number(buy_number); // 商品数型
    buyOrder.setBuy_unit(buy_unit); // 单位
    buyOrder.setBuy_price(gdsy_money); // 单笔金额
    buyOrder.setBuy_amount(buyLot); // 手数
    buyOrder.setBuy_brokerage(totalPoundage); // 总手续费
    buyOrder.setBuy_all_price(orderAmount); // 订单金额
    buyOrder.setConfirm("2"); // 建仓中
    buyOrder.setIs_use("1"); // 可用
    buyOrder.setItem_id(itemId); // 商品ID
    
    buyOrder.setGdsy_buy_ratio(gdsy_ratio); // 比率
    // 点位期权
    buyOrder.setTrading_rule(TradeUtil.Rule_PointOptions);
    buyOrder.setBuy_itemname_alias(alias); // 别名

    long end = System.currentTimeMillis();
    
    boolean rst = false;
    try {
      rst = hangingDelistedService.buy(buyOrder);
    } catch (Exception e) {
      logger.error("写建仓队列异常", e);
    }
    if (rst) {
      msg.setSuccess(true);
      msg.setMsg("众筹购买已受理"); // backMsg.setMsg("建仓已受理");
    } else {
      msg.setSuccess(false);
      msg.setMsg("众筹购买失败"); // backMsg.setMsg("建仓失败");
    }
    if (logger.isInfoEnabled()) {
      long time = end - start;
      System.out.println("挂摘牌WEB建仓所需时间：" + time + " 订单号：" + oid);
    }
    return msg;
  }
}

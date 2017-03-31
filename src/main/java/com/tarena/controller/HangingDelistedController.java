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
import com.tarena.mongo.service.MongodbBaseDao;
import com.tarena.service.IHangingDelistedService;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.utils.IdentificationUtil;
import com.tarena.trade.utils.TradeUtil;

/**
 * 挂摘牌
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/hangingDelisted")
public class HangingDelistedController extends BaseController {

  private static Logger logger = Logger.getLogger(HangingDelistedController.class);
  
  @Autowired
  private IHangingDelistedService hangingDelistedService;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private MongodbBaseDao<Object> mongodbBaseDao;
  
  @Autowired
  private QuotationComponent quotation;
  
  @Autowired
  private DistributeLock distributeLock;
  
  @RequestMapping("/buy")
  @ResponseBody
  public Object buy(@Session(create = false)SessionProvider session,@RequestParam Map<String,Object> map){
    //建仓
    //建仓开始
    long start = System.currentTimeMillis();
    
    int buyLot = Integer.parseInt((String) map.get("lot"));
    String buyType = map.get("type").toString();
    String itemId = map.get("id").toString();
    
    //建仓验证开始
    //购买手数必须大于0
    
    if(buyLot < 0){
      msg.setSuccess(false);
      msg.setMsg("购买数量必须大于0");
      return msg;
    }
    if(!(buyType.equals("buy")|| buyType.equals("sell"))){
      msg.setSuccess(false);
      msg.setMsg("交易方向必须为购物或赊货");//必须为买涨或买跌
      return msg;
    }
    if(session == null){
      msg.setSuccess(false);
      msg.setMsg("请登录后操作");
      return msg;
    }
    
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    if(userInfo == null){
      msg.setSuccess(false);
      msg.setMsg("请登录后操作");
      return msg;
    }
    
    UserInfo mUserInfo = mongoTemplate.findOne(new Query(Criteria.where("user_id").is(userInfo.getUser_id())), UserInfo.class, "t_wp_front_user_login");
    if(!mUserInfo.getIs_use().equals("1")){
      msg.setSuccess(false);
      msg.setMsg("您的账户不可用");
      return msg;
    }
    //代理商暂停交易
    String disableAgents = stringRedisTemplate.opsForValue().get("disable_agent_id");
    String markAgent = "#"+userInfo.getAgent_id()+"#";
    if(StringUtils.isNotBlank(disableAgents)&& disableAgents.contains(markAgent)){
      msg.setSuccess(false);
      msg.setMsg("当前不可交易");
      return msg;
    }
    
    //获取商品信息
    String itemIdStr = stringRedisTemplate.opsForValue().get("gzp_"+userInfo.getAgent_id()+"_"+itemId);
    if(StringUtils.isBlank(itemIdStr)){
      msg.setSuccess(false);
      msg.setMsg("交易商品不存在");
      return msg;
    }
    
    String itemInfoStr = stringRedisTemplate.opsForValue().get("gzp_goods_"+itemId);
    if(StringUtils.isBlank(itemInfoStr)){
      msg.setSuccess(false);
      msg.setMsg("交易商品不存在");
      return msg;
    }
    JSONObject json = JSONObject.parseObject(itemInfoStr);
    String is_use = json.get("is_use").toString();
    if(!is_use.equals("1")){
      msg.setSuccess(false);
      msg.setMsg("交易商品不可用");
      return msg;
    }
    
    String gzp_name = json.getString("gzp_name");//商品名称
    String gzp_type = json.getString("gzp_type");//商品数型
    String gzp_code = json.getString("gzp_code");//商品代码
    String buy_number = json.getString("gzp_number");//商品数型
    String buy_unit = json.getString("gzp_unit");//单位
    double gzp_money = Double.valueOf(json.getString("gzp_money"));//金额
    double gzp_poundage = Double.valueOf(json.getString("gzp_poundage"));//手续费
    double gzp_profit = Double.valueOf(json.getString("gzp_profit"));//波动盈亏
    int maxLot = Integer.valueOf(json.getString("gzp_lot"));//最大手数
    String buy_date = json.getString("gzp_buy_no_date");//日期
    String buy_time = json.getString("gzp_buy_no_time");//时间
    String alias = json.getString("gzp_alias");//别名

    //交易时间判断
    try {
      if(!TradeUtil.checkTradeDate(buy_date, buy_time)){
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
    //已购买数量求和
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(
            Criteria.where("user_id").is(userInfo.getUser_id()).and("trading_rule").is("01")
            .and("buy_itemcode").is(gzp_code).and("buy_type").is("buy").and("confirm").ne("0").and("is_zc").ne("1")),
            Aggregation.group("buy_itemcode").sum("buy_amount").as("holder_lot"));
    AggregationResults<AggregationContent> resultLot = mongoTemplate.aggregate(agg, "TradeBuyOrderMongodb", AggregationContent.class);
    List<AggregationContent> list = resultLot.getMappedResults();
    long holderLot = 0;
    if(list.size() > 0){
      holderLot = list.get(0).getHolder_lot();
    }
    if(holderLot + buyLot > maxLot){
      msg.setSuccess(false);
      msg.setMsg("当前商品，购买数量不能大于"+maxLot + "笔");//"当前商品，建仓手数不能大于"
      return msg;
    }
    
    //购买金额判断
    double totalPrice = gzp_money * buyLot;
    double totalPoundate = gzp_poundage * buyLot;
    double orderAmount = totalPoundate + totalPrice;
    
    //获取行情数据
    double buyPoint = quotation.getBuyPoint(gzp_type, buyType);
    
    //更新用户余额
    String lockKeyActivityLuckyDrawOfWallet = distributeLock.getLock("wallet", userInfo.getUser_id());
    if(lockKeyActivityLuckyDrawOfWallet == null){
      msg.setSuccess(false);
      msg.setMsg("取用户金额锁失败");
      return msg;
    }
    
    try {
      String redisKey = "wallet_"+userInfo.getUser_id();
      String walletText = stringRedisTemplate.opsForValue().get(redisKey);//用户钱包
      if(walletText == null){
        msg.setSuccess(false);
        msg.setMsg("您的余额获取失败");
        return msg;
      }
      double userWallet = Double.valueOf(walletText);
      if(userWallet > 0 && userWallet >= orderAmount){
        double newWallet = userWallet - orderAmount;
        //更新redis
        stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(newWallet));
      } else {
        msg.setSuccess(false);
        msg.setMsg("您的余额不足");
        msg.setCode("000001");
        return msg;
      }
    } finally {
      distributeLock.releaseLock("wallet", userInfo.getUser_id(), lockKeyActivityLuckyDrawOfWallet);
    }
    
    //挂摘牌建仓订单
    String oid = IdentificationUtil.generateId();
    TradeBuyOrderMongodb buyOrder = new TradeBuyOrderMongodb();
    double zsPoint = 100;//默认止损点
    if(gzp_type.contains("cu")){
      zsPoint = 1000;//铜的默认止损点
    }else if(gzp_type.contains("mo")){
      zsPoint = 50;//美蓝莓默认止损点
    }
    if(buyType.equals("buy")){
      buyOrder.setGzp_sell_zs_point(buyPoint - zsPoint);
    }else{
      buyOrder.setGzp_sell_zs_point(buyPoint + zsPoint);//买跌就不能让其涨，止损点位就在买的基础上加
    }
    buyOrder.setOrder_id(oid);//建仓Id
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
    buyOrder.setBuy_itemtype(gzp_type); // 商品数型
    buyOrder.setBuy_itemcode(gzp_code); // 商品CODE
    buyOrder.setBuy_itemname(gzp_name); // 商品名称
    buyOrder.setBuy_number(buy_number); // 商品数型
    buyOrder.setBuy_unit(buy_unit); // 单位
    buyOrder.setBuy_price(gzp_money); // 单笔金额
    buyOrder.setBuy_amount(buyLot); // 手数
    buyOrder.setBuy_brokerage(totalPoundate); // 总手续费
    buyOrder.setBuy_all_price(orderAmount); // 订单金额
    buyOrder.setGzp_profit(gzp_profit); // 波动盈亏
    
    buyOrder.setConfirm("2");//建仓中
    buyOrder.setIs_use("1");//可用
    buyOrder.setItem_id(itemId);//商品Id
    buyOrder.setTrading_rule("01");//挂摘牌
    buyOrder.setBuy_itemname_alias(alias);//别名
    
    
    long end = System.currentTimeMillis();//建仓完成时间
    boolean rst = false;
    try {
      rst = hangingDelistedService.buy(buyOrder);
    } catch (Exception e) {
      logger.error("写生成队列异常",e);
    }
    if(rst){
      msg.setSuccess(true);
      msg.setMsg("众筹购买已受理");//建仓已受理
    }else{
      msg.setSuccess(false);
      msg.setMsg("众筹购买失效");//建仓失败
    }
    if(logger.isInfoEnabled()){
      long time = end - start;
      System.out.println("挂摘牌WEB建仓所需时间:"+time + "订单号:"+oid);
    }
    
    return msg;
  }
  
  
  /**
   * 手动平仓(挂摘牌)
   **/
  @RequestMapping(value = "/manuallyClosePosition")
  @ResponseBody
  public Object manuallyClosePosition(@Session(create = false) SessionProvider session,
      @RequestParam(value = "order_id") String order_id) {
    try {
      // 获取用户信息
      UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);

      int rst = hangingDelistedService.sell(order_id, userInfo.getUser_id());
      if (rst == 1) {
        msg.setSuccess(true);
        msg.setMsg("系统已受理您的退款申请"); // backMsg.setMsg("系统已受理您的平仓申请");
      } else if (rst == -1) {
        msg.setSuccess(false);
        msg.setMsg("系统正在退款中"); // backMsg.setMsg("系统正在平仓中");
      } else if (rst == 0) {
        msg.setSuccess(false);
        msg.setMsg("推送数据异常"); // backMsg.setMsg("推送点位异常");
      }
    } catch (Exception e) {
      e.printStackTrace();
      msg.setSuccess(false);
      msg.setCode("04");
      msg.setMsg("退款失败"); // backMsg.setMsg("手动平仓失败");
    }
    return msg;
  }
}

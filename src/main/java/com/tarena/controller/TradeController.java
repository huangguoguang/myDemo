package com.tarena.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.common.component.session.support.Session;
import com.tarena.entity.UserInfo;
import com.tarena.service.IPaymentTailService;
import com.tarena.service.ITradeService;
import com.tarena.trade.utils.TradeUtil;
import com.tarena.util.Pagination;

@Controller
@RequestMapping("/trade")
public class TradeController extends BaseController{
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private ITradeService tradeService;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private IPaymentTailService paymentTailService;

  @RequestMapping("/itemInfo")
  @ResponseBody
  public Object getItemInfo(@Session(create = false) SessionProvider session,@RequestParam Map<String,Object> map){
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    String gzp_item = stringRedisTemplate.opsForValue().get(userInfo.getAgent_id()+"_gzp");
    String dwqq_item = stringRedisTemplate.opsForValue().get(userInfo.getAgent_id()+"_dwqq");
    String gdsy_item = stringRedisTemplate.opsForValue().get(userInfo.getAgent_id()+"_gdsy");
    String zc_item = stringRedisTemplate.opsForValue().get(userInfo.getAgent_id()+"_zc");
    JSONArray json = new JSONArray();
    if(gzp_item != null){
      json.add(JSONObject.parse(gzp_item));
    }
    if(gdsy_item != null) {
      json.add(JSONObject.parse(gdsy_item));
    }
    if(dwqq_item != null) {
      json.add(JSONObject.parse(dwqq_item));
    }
    if(zc_item != null) {
      json.add(JSONObject.parse(zc_item));
    }
    return json;
  }
  

  /**
   * 实时数据
   * 
   */
  @RequestMapping("/topdata")
  @ResponseBody
  public Object getTopData() {
    String ag6 = stringRedisTemplate.opsForValue().get("ag6");
    String cu10 = stringRedisTemplate.opsForValue().get("cu10");
    String oil12 = stringRedisTemplate.opsForValue().get("oil12");
    String blueberry13 = stringRedisTemplate.opsForValue().get("mo13");
    String gas14 = stringRedisTemplate.opsForValue().get("trq14"); 
    JSONObject json = new JSONObject();
    json.put("6", JSONObject.parse(ag6));
    json.put("10", JSONObject.parse(cu10));
    json.put("12", JSONObject.parse(oil12));
    json.put("13", JSONObject.parse(blueberry13));
    json.put("14", JSONObject.parse(gas14)); // 天燃气
    return json;
  }
  
  /**
   * 获取用户每个品种的交易数分组
   * @param session
   * @param map
   * @return
   */
  @RequestMapping("/userLot")
  @ResponseBody
  public Object getUserLot(@Session(create = false) SessionProvider session){
    return tradeService.queryUserLot(session);
  }
  
  @RequestMapping("/totalOrder")
  @ResponseBody
  public Object getTotalOrder(@Session(create = false) SessionProvider session){
    return tradeService.totalOrder(session);
  }
  
  /**
   * 获取所有
   * @param session
   * @param map
   * @return
   * @throws ParseException
   */
  @RequestMapping("/buyOrder")
  @ResponseBody
  public Object getBuyOrder(@Session(create=false)SessionProvider session,@RequestParam Map<String,Object> map) throws ParseException{
    UserInfo userInfo = (UserInfo)session.getAttribute(UserInfo.sessionKey);
    return tradeService.queryBuyOrder(userInfo.getUser_id());
  }
  
  /**
   * 获取交易历史订单信息（有分页，交易历史，以平仓时间为条件）
   * @param session
   * @param page
   * @param row
   * @param map
   * @return
   * @throws ParseException
   */
  @SuppressWarnings("rawtypes")
  @RequestMapping("/sellOrder")
  @ResponseBody
  public Object getSellOrder(@Session(create = false) SessionProvider session,
      @RequestParam(defaultValue = "0",required = false) Integer page,
      @RequestParam(defaultValue = "10", required = false) Integer row,
      @RequestParam Map<String,Object> map)throws ParseException{
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, 0);
    String lastDate = format.format(cal.getTime());//得到今天的日期
    cal.add(Calendar.DATE, -30);
    String firstDate = format.format(cal.getTime());//得到30天以前的日期
    
    try {
      return tradeService.querySellOrder(userInfo.getUser_id(),firstDate,lastDate,"sell_time",page,row,TradeUtil.Mongodb_Table_TradeSellOrder);
    } catch (Exception e) {
      return new Pagination<Map>(0, 10, 0);
    }
  }
  
  /**
   * 获取众筹订单
   * @param session
   * @return
   * @throws ParseException 
   */
  @RequestMapping("/CrowdfundingBuyOrder")
  @ResponseBody
  public Object getCrowdfundingBuyOrder(@Session(create = false)SessionProvider session,
                                        @RequestParam Map<String ,Object> map) throws ParseException{
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    return tradeService.queryCrowdfundingBuyOrder(userInfo.getUser_id());
  }
  
  @RequestMapping("/getbuyById")
  @ResponseBody
  public Object getBuyById(@Session(create = false)SessionProvider session,
                  @RequestParam Map<String,Object> map){
    String order_id = map.get("order_id").toString();
    UserInfo userInfo = (UserInfo)session.getAttribute(UserInfo.sessionKey);
    Query query = new Query();
    query.addCriteria(Criteria.where("user_id").is(userInfo.getUser_id()).and("order_id").is(order_id));
    return mongoTemplate.findOne(query, Map.class, TradeUtil.Mongodb_Table_TradeBuyOrder);
  }
  
  @RequestMapping("/paymentTail")
  @ResponseBody
  public Object paymentTail(@Session(create = false)SessionProvider session,
      @RequestParam Map<String,Object> map){
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    
    String user_id = userInfo.getUser_id();
    String name = map.get("name").toString();
    String mobile = map.get("mobile").toString();
    String order_id = map.get("order_id").toString();
    String address = map.get("address").toString();
    
    paymentTailService.paymentTail(user_id,order_id,name,mobile,address);
    return null;
  }
  
  /**
   * 行情K线
   * @param type
   * @param min
   * @return
   */
  @RequestMapping("/kline")
  @ResponseBody
  public Object getKLine(@RequestParam(value = "type")String type,@RequestParam(value = "min") String min){
    String lineKey = TradeUtil.convertLineKey(type,min);
    return stringRedisTemplate.opsForValue().get(lineKey);
  }
  
  /**
   * 查询用户购物订单详细信息
   * @param session
   * @param shoppingOrderId
   * @return
   */
  @RequestMapping("/getGoodDetail")
  @ResponseBody
  public Object getGoodDetail(@Session(create = false) SessionProvider session,@RequestParam(value = "shopping_order_id") String shoppingOrderId){
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    String user_id = userInfo.getUser_id();
    Map<String,Object> queryMap = new HashMap<String, Object>();
    queryMap.put("user_id", user_id);
    queryMap.put("shopping_order_id", shoppingOrderId);
    return paymentTailService.getGoodDetail(queryMap);
  }
}

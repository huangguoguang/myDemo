package com.tarena.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.entity.AggregationContent;
import com.tarena.entity.UserInfo;
import com.tarena.mongo.service.MongodbBaseDao;
import com.tarena.service.ITradeService;
import com.tarena.trade.utils.TradeUtil;
import com.tarena.util.NumberUtil;
import com.tarena.util.Pagination;

@Service
public class TradeServiceImpl implements ITradeService {
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private StringRedisTemplate redisTemplate;
  
  @SuppressWarnings("rawtypes")
  @Autowired
  private MongodbBaseDao<Map> mongodbBaseDao;

  @Override
  public Object queryUserLot(SessionProvider session) {
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("user_id").is(userInfo.getUser_id()).and("confirm").ne("0")),
        Aggregation.group("trading_rule","buy_number","buy_type").sum("buy_amount").as("holder_lot"));
    AggregationResults<AggregationContent> resultLot = mongoTemplate.aggregate(agg, "TradeBuyOrderMongodb", AggregationContent.class);
    List<AggregationContent> listOne = resultLot.getMappedResults();
    String listMutiSTR = redisTemplate.opsForValue().get("trade_volume");
    JSONArray listMuti = (JSONArray) JSONArray.parse(listMutiSTR);
    Map<String, Object> reMap = new HashMap<String, Object>();
    reMap.put("total", listMuti);
    reMap.put("holder", listOne);
    
    return reMap;
  }

  /* 
   * 
   * 用户订单总数量  
   */
  @Override
  public Object totalOrder(SessionProvider session) {
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    return mongoTemplate.count(new Query(Criteria.where("user_id").is(userInfo.getUser_id()).and("confirm").ne("0")), "TradeBuyOrderMongodb");
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public List<Map> queryBuyOrder(String user_id) {
    Query query = new Query(Criteria.where("user_id").is(user_id).and("confirm").ne("0").and("trading_rule").ne("04"));
    query.with(new Sort(Direction.DESC, "buy_time"));
    //建仓表
    List<Map> orderList = mongoTemplate.find(query, Map.class, TradeUtil.Mongodb_Table_TradeBuyOrder);
    //止盈止损表
    List<Map> spslList = mongoTemplate.find(query, Map.class,TradeUtil.Mongodb_Table_StopProfitStopLoss);
    //日期转换
    for(Map orderMap:orderList){
      if(orderMap.get("trading_rule").toString().equals("02")){
         orderMap.put("now_date", new Date());
       }
      }
    for(Map orderMap:orderList){
      for(Map spslMap:spslList){
        if(orderMap.get("order_id").equals(spslMap.get("order_id"))){
          orderMap.put("zyzs_confirm", spslMap.get("confirm"));
          orderMap.put("sell_point", spslMap.get("sell_point"));
        }
      }
    }
     return orderList;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Pagination<Map> querySellOrder(String user_id, String firstDate,
      String lastDate, String column, int page, int pageSize, String collection)
      throws ParseException {
    Pagination<Map> sellOrder = mongodbBaseDao.queryOrder(user_id,firstDate,lastDate,column,page,pageSize,Map.class,collection);
    List<Map> list = sellOrder.getRows();
    for(Map tradeSellOrderMongodb : list) {
      tradeSellOrderMongodb.put("sell_point", NumberUtil.fotmatNum((Double)tradeSellOrderMongodb.get("sell_point"), 3));
    }
    return sellOrder;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public List<Map> queryCrowdfundingBuyOrder(String user_id) throws ParseException {
    //查询建仓成功和建仓中的订单
    Query query = new Query();
    query.addCriteria(Criteria.where("user_id").is(user_id).and("confirm").ne("0").and("trading_rule").is("04"));
    query.with(new Sort(Direction.DESC, "buy_time"));
    return mongoTemplate.find(query, Map.class,TradeUtil.Mongodb_Table_TradeBuyOrder);
  }

}

package com.tarena.mq.test;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.tarena.baseTest.SpringTestCase;
import com.tarena.lock.DistributeLock;
import com.tarena.trade.entity.StopProfitStopLossMongodb;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.entity.TradeSellOrderMongodb;
import com.tarena.trade.utils.TradeUtil;

public class TestTradeBuy extends SpringTestCase{

  @Autowired
  private AmqpTemplate amqpTemplate;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private DistributeLock distributeLock;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  
  @Test
  @Ignore
  public void test(){
    amqpTemplate.convertAndSend("wp.tarena.trade.in", "wp.rk.trade.hd.buy", "wo yao buy");
  }
  
  //挂摘牌手动平仓
  @Test
  @Ignore
  public void testSell(){
    StopProfitStopLossMongodb stopProfitStopLossMongodb = mongoTemplate.findOne(
        Query.query(Criteria.where("order_id").is("c22108576d7d40eb8283b3172b357da4")), StopProfitStopLossMongodb.class, TradeUtil.Mongodb_Table_StopProfitStopLoss);
//    Query query = new Query();
//    query.addCriteria(Criteria.where("order_id").is("c22108576d7d40eb8283b3172b357da4"));
//    TradeBuyOrderMongodb buyOrder = mongoTemplate.findOne(query, TradeBuyOrderMongodb.class, TradeUtil.Mongodb_Table_TradeBuyOrder);

    String pointText = stringRedisTemplate.opsForValue().get(stopProfitStopLossMongodb.getBuy_itemtype());
    if(StringUtils.isBlank(pointText)){
      System.out.println("error");
    }
    double current_point = Double.valueOf(JSONObject.parseObject(pointText).get("ws_new_point").toString()); // 当前点位
    
    Date date = new Date();
    
    TradeSellOrderMongodb tradeSellOrderMongodb = TradeUtil.convertBuyOrderMongodb(mongoTemplate.findOne(
        Query.query(Criteria.where("order_id").is(stopProfitStopLossMongodb.getOrder_id())), TradeBuyOrderMongodb.class, TradeUtil.Mongodb_Table_TradeBuyOrder));
    
    tradeSellOrderMongodb.setSell_point(current_point); // 平仓点位
    tradeSellOrderMongodb.setSell_time(date); // 平仓时间
    tradeSellOrderMongodb.setSell_type("manuallySell"); // 平仓类型->手动平仓
    amqpTemplate.convertAndSend("wp.tarena.trade.out", "wp.rk.trade.hd.sell",JSONObject.toJSON(tradeSellOrderMongodb));
  }
  
  //固定收益自动平仓
  @Test
  public void testBinaryOptionSell(){
    //b3a0f76802f042ad97365f97d79bc7a0
    Query query = new Query();
    query.addCriteria(Criteria.where("order_id").is("40c3d2a236884d24a5517115a635c645"));
    TradeBuyOrderMongodb buyOrder = mongoTemplate.findOne(query, TradeBuyOrderMongodb.class, TradeUtil.Mongodb_Table_TradeBuyOrder);
    
    TradeSellOrderMongodb sellOrder = TradeUtil.convertBuyOrderMongodb(buyOrder);
    amqpTemplate.convertAndSend("wp.tarena.trade.out", "wp.rk.trade.hd.sell",JSONObject.toJSON(sellOrder));
  }
  }

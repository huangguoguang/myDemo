package com.tarena.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tarena.lock.DistributeLock;
import com.tarena.mongo.service.MongodbBaseDao;
import com.tarena.service.IHangingDelistedService;
import com.tarena.trade.entity.StopProfitStopLossMongodb;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.entity.TradeSellOrderMongodb;
import com.tarena.trade.utils.TradeUtil;

@Service
public class HangingDelistedServiceImpl implements IHangingDelistedService {
  @Autowired
  private DistributeLock distributeLock;
  @Autowired
  private MongodbBaseDao<Object> mongodbBaseDao;
  @Autowired
  private AmqpTemplate amqpTemplate;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Override
  public boolean buy(TradeBuyOrderMongodb buyOrder) {
    String lockKeyActivityLuckydraw = distributeLock.getLock("buy", buyOrder.getUser_id());
    if(lockKeyActivityLuckydraw == null){
      return false;
    }
    try {
      //建仓订单写进mongodb
      mongodbBaseDao.save(buyOrder, "TradeBuyOrderMongodb");
      //建仓订单写进MQ
      amqpTemplate.convertAndSend("wp.tarena.trade.in", "wp.rk.trade.hd.buy", JSONObject.toJSON(buyOrder));
      return true;
    } finally {
      distributeLock.releaseLock("buy", buyOrder.getUser_id(), lockKeyActivityLuckydraw);
    }
  }

  //手动平仓（挂摘牌）
  @Override
  public int sell(String order_id, String user_id) {
    String lockSellKey = distributeLock.getLock("sell", order_id);
    if(lockSellKey == null) {
      return -1;
    }
    try{
      StopProfitStopLossMongodb stopProfitStopLossMongodb = mongoTemplate.findOne(
          Query.query(Criteria.where("order_id").is(order_id)), StopProfitStopLossMongodb.class, TradeUtil.Mongodb_Table_StopProfitStopLoss); //要加userid
      
      if (stopProfitStopLossMongodb == null
          || stopProfitStopLossMongodb.getOrder_id() == null
          || !("1".equals(stopProfitStopLossMongodb.getConfirm()))) { // [0无效 1 有效 2建仓中 3待平仓 4平仓中]
        return -1;
      }
      String pointText = stringRedisTemplate.opsForValue().get(stopProfitStopLossMongodb.getBuy_itemtype());
      if(StringUtils.isBlank(pointText)){
        return 0;
      }
      double current_point = Double.valueOf(JSONObject.parseObject(pointText).get("ws_new_point").toString()); // 当前点位
      
      Date date = new Date();
      
      TradeSellOrderMongodb tradeSellOrderMongodb = TradeUtil.convertBuyOrderMongodb(mongoTemplate.findOne(
          Query.query(Criteria.where("order_id").is(stopProfitStopLossMongodb.getOrder_id())), TradeBuyOrderMongodb.class, TradeUtil.Mongodb_Table_TradeBuyOrder));
      
      tradeSellOrderMongodb.setSell_point(current_point); // 平仓点位
      tradeSellOrderMongodb.setSell_time(date); // 平仓时间
      tradeSellOrderMongodb.setSell_type("manuallySell"); // 平仓类型->手动平仓
      
      Criteria updateCriteria = new Criteria();
      updateCriteria.andOperator(Criteria.where("order_id").is(stopProfitStopLossMongodb.getOrder_id()), Criteria.where("user_id").is(user_id));
      Update update  = new Update();
      update.set("sell_point", current_point); // 平仓点位
      update.set("confirm", "4");              // 更新状态为4[0无效 1 有效 2建仓中 3待平仓 4平仓中]
      update.set("sell_date", date);           // 平仓时间
      update.set("sell_type", "manuallySell"); // 平仓类型->手动平仓
      mongoTemplate.updateMulti(new Query(updateCriteria),update, "StopProfitStopLossMongodb");
      
     // 写平仓队列
      amqpTemplate.convertAndSend("wp.tarena.trade.out", "wp.rk.trade.hd.sell", JSONObject.toJSON(tradeSellOrderMongodb));
      return 1;
    }finally{
      distributeLock.releaseLock("sell", order_id,lockSellKey);
    }
  }

}

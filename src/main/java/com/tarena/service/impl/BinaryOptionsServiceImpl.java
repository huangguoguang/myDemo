package com.tarena.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tarena.lock.DistributeLock;
import com.tarena.mongo.service.MongodbBaseDao;
import com.tarena.service.IBinaryOptionsService;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.utils.TradeUtil;

@Service
public class BinaryOptionsServiceImpl implements IBinaryOptionsService {
  
  @Autowired
  private DistributeLock distributeLock;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private MongodbBaseDao<Object> mongoDbBaseDao;

  @Autowired
  private AmqpTemplate amqpTemplate;
  @Override
  public boolean buy(TradeBuyOrderMongodb buyOrder) {
    String lockKeyActivityLuckydraw = distributeLock.getLock("buy", buyOrder.getUser_id());
    if(lockKeyActivityLuckydraw == null){
      return false;
    }
    try{
    //建仓订单写进mongo
      mongoDbBaseDao.save(buyOrder, TradeUtil.Mongodb_Table_TradeBuyOrder);
      //建仓订单写写amqp
      amqpTemplate.convertAndSend("wp.tarena.trade.in", "wp.rk.trade.2opt.buy", JSONObject.toJSON(buyOrder));
      //待平仓订单写延时
      amqpTemplate.convertAndSend("wp.tarena.trade.in", "wp.rk.trade.2opt.delay."+buyOrder.getGdsy_buy_xz_time(), 
          JSONObject.toJSON(TradeUtil.convertBuyOrderMongodb(buyOrder)));
      return true;
    }finally{
      distributeLock.releaseLock("buy",buyOrder.getUser_id(), lockKeyActivityLuckydraw);
    }
    
  }

}

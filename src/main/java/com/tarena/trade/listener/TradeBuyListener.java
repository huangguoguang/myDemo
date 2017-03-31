package com.tarena.trade.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tarena.service.IProcessOrderService;
import com.tarena.trade.entity.TradeBuyOrderMongodb;

@Service
public class TradeBuyListener {

  @Autowired
  private ThreadPoolTaskExecutor taskExecutor;
  
  @Autowired
  private IProcessOrderService processOrderService;
  
  public void execute(JSONObject data){
    TradeBuyOrderMongodb buyOrder = JSONObject.parseObject(data.toJSONString(), TradeBuyOrderMongodb.class);
    taskExecutor.execute(new TradeBuyExecutor(processOrderService,buyOrder));
  }
}

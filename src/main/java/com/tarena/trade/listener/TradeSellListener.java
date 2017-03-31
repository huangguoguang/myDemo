package com.tarena.trade.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tarena.service.IProcessOrderService;
import com.tarena.trade.entity.TradeSellOrderMongodb;

@Service
public class TradeSellListener {
  @Autowired
  private ThreadPoolTaskExecutor taskExecutor;
  
  @Autowired
  private IProcessOrderService processOrderService;
  
  @Autowired
  private TradeProcess tradeProcess;
  
  public void execute(JSONObject data){
    System.out.println("执行");
    TradeSellOrderMongodb sellOrder = JSONObject.parseObject(data.toJSONString(), TradeSellOrderMongodb.class);
    //执行线程平仓
    taskExecutor.execute(new TradeSellExecutor(processOrderService,sellOrder,tradeProcess));
  }
}

package com.tarena.trade.listener;

import org.apache.log4j.Logger;

import com.tarena.service.IProcessOrderService;
import com.tarena.trade.entity.TradeSellOrderMongodb;

public class TradeSellExecutor implements Runnable {
  
  private static Logger logger = Logger.getLogger(TradeSellExecutor.class);
  private IProcessOrderService processOrderService;
  
  private TradeSellOrderMongodb sellOrder;
  
  private TradeProcess tradeProcess;
  
  

  public TradeSellExecutor(IProcessOrderService processOrderService,
      TradeSellOrderMongodb sellOrder, TradeProcess tradeProcess) {
    this.sellOrder = sellOrder;
    this.processOrderService = processOrderService;
    this.tradeProcess = tradeProcess;
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    
    tradeProcess.preProcessSellOrder(sellOrder,processOrderService);
    
    long end = System.currentTimeMillis();
    
    if(logger.isInfoEnabled()){
      long time = end - start ;
      System.out.println("平仓MQ消费所需时间：" + time);
    }
  }

}

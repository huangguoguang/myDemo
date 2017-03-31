package com.tarena.util.test;

import com.tarena.trade.entity.TradeSellOrderMongodb;

public class TestNull {
  public static void main(String[] args) {
    TradeSellOrderMongodb sellOrder = new TradeSellOrderMongodb();
    sellOrder.setAgent_id("34");
    System.out.println(sellOrder.getRollback());
    System.out.println("2".equals(sellOrder.getRollback()));
    System.out.println("2".equals(null));
  }
}

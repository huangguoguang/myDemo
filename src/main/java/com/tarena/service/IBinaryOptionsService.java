package com.tarena.service;

import com.tarena.trade.entity.TradeBuyOrderMongodb;

public interface IBinaryOptionsService {
  public boolean buy(TradeBuyOrderMongodb buyOrder);
}

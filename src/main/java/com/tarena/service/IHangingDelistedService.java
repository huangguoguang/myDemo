package com.tarena.service;

import com.tarena.trade.entity.TradeBuyOrderMongodb;

public interface IHangingDelistedService {

  /**
   * 挂摘牌建仓
   * @param buyOrder
   * @return
   */
  boolean buy(TradeBuyOrderMongodb buyOrder);

  /**挂摘牌平仓
   * @param order_id
   * @param user_id
   * @return
   */
  public int sell(String order_id, String user_id);

}

package com.tarena.service;

import java.util.Map;

public interface IPaymentTailService {

  public int paymentTail(String user_id, String order_id, String name, String mobile,String address);

  /**
   * 查询用户购物订单详细信息
   * @param queryMap
   * @return
   */
  public Object getGoodDetail(Map<String, Object> queryMap);
  
}

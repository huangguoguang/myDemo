package com.tarena.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarena.dao.IPaymentTailDao;
import com.tarena.lock.DistributeLock;
import com.tarena.service.IPaymentTailService;

@Service
public class PaymentTailServiceImpl implements IPaymentTailService {

  @Autowired
  private DistributeLock distributeLock;
  
  @Autowired
  private IPaymentTailDao paymentTailDao;
  
  @Override
  public int paymentTail(String user_id, String order_id, String name,
      String mobile, String address) {
    String lockMysqlWallet = distributeLock.getLock("mysql_wallet", user_id);
    try{
      
    }finally{
      
    }
    return 0;
  }

  @Override
  public Object getGoodDetail(Map<String, Object> queryMap) {
    
    return paymentTailDao.getGoodDetail(queryMap);
  }

}

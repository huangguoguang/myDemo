package com.tarena.service;

import java.util.Map;

import com.tarena.entity.UserInfo;

public interface IUserAddrService {

  /**
   * 获取默认收货地址
   * @param user_id
   * @return
   */
  public Map<String,Object> getDefaultAddr(UserInfo userInfo);
}

package com.tarena.dao;

import java.util.Map;

import com.tarena.entity.UserInfo;

public interface IUserAddrDao {

  /**
   * 查询默认收货地址
   * @param user_id
   * @return
   */
  public Map<String, Object> getDefaultAddr(UserInfo userInfo);

}

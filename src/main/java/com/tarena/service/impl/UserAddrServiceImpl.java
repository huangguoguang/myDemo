package com.tarena.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarena.dao.IUserAddrDao;
import com.tarena.entity.UserInfo;
import com.tarena.service.IUserAddrService;

@Service
public class UserAddrServiceImpl implements IUserAddrService {
  @Autowired
  private IUserAddrDao addrDao;

  @Override
  public Map<String, Object> getDefaultAddr(UserInfo userInfo) {
    return addrDao.getDefaultAddr(userInfo);
  }

}

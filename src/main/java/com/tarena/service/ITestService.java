package com.tarena.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tarena.entity.User;
import com.tarena.exception.BusinessException;

public interface ITestService {

  @Transactional(rollbackFor= {BusinessException.class},propagation = Propagation.REQUIRED)
  public boolean doUser(User user) throws BusinessException;
}

package com.tarena.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarena.dao.ITestDao;
import com.tarena.entity.User;
import com.tarena.exception.BusinessException;
import com.tarena.service.ITestService;

@Service
public class TestServiceImpl implements ITestService{
  @Autowired
  private ITestDao testDao;

  @Override
  public boolean doUser(User user) throws BusinessException {
    try {
      if(testDao.insertUser(user) != 1){
        throw new BusinessException("新增用户失败");
      }
      user.setUsername("张三小");
      user.setAddress("大江大湖大武汉");
      if(testDao.updateUser(user) != 1){
        System.out.println("更新失败");
        throw new BusinessException("更新用户失败");
      }
      User user2 = new User();
      user2.setUsername("工a");
      user2.setAddress("武汉武汉");
      if(testDao.updateUser(user2) != 1){
        throw new BusinessException("更新用户2失败");
      }
      System.out.println("操作正常");
    } catch (Exception e) {
      System.out.println(e);
      throw new BusinessException("操作失败");
    } finally{
      System.out.println("执行完毕");
    }
    return true;
  }

}

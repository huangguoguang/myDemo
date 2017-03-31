package com.tarena.transaction.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarena.baseTest.SpringTestCase;
import com.tarena.entity.User;
import com.tarena.exception.BusinessException;
import com.tarena.service.ITestService;

public class TestTransaction extends SpringTestCase{

  @Autowired
  private ITestService testService;
  
  @Test
  public void test(){
    User user = new User();
    user.setAddress("上海");
    user.setUsername("黄");
    user.setAge(20);
    boolean rst =false;
    try {
      rst = testService.doUser(user);
    } catch (BusinessException e) {
      System.out.println("操作失败");
    }
    if(rst){
      System.out.println("执行确认");
    } else {
      System.out.println("执行回滚");
    }
  }
}

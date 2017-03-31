package com.tarena.util.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarena.baseTest.SpringTestCase;
import com.tarena.lock.DistributeLock;

public class TestLock extends SpringTestCase {

  @Autowired
  private DistributeLock distributeLock;
  
  @Test
  public void testGetAndReleaseLock(){
    
    String lockKey = distributeLock.getLock("test", "94BF70B561904A5994F8022856718BE2");
    System.out.println(lockKey);
    if(lockKey == null){
      System.out.println("取锁失败");
    } else {
      try{
        System.out.println("取锁成功，执行操作");
      }finally{
        boolean rst = distributeLock.releaseLock("test", "94BF70B561904A5994F8022856718BE2", lockKey);
        if(rst){
          System.out.println("释放锁成功");
        } else{
          System.out.println("释放锁失败");
        }
      }
    }
  }
}

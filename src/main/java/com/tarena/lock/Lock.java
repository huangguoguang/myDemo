package com.tarena.lock;

import java.util.concurrent.TimeUnit;

public interface Lock {
  /**
   * 阻塞性的获取锁，不响应中断
   */
  void lock();
  
  /**
   * 阻塞性的获取锁，响应中断
   * @throws InterruptedException
   */
  void lockInterruptibly() throws InterruptedException;
  
  
  /**
   * 尝试性的获取锁，获取不到立即返回，不阻塞
   * @return
   */
  boolean tryLock();

  
  /**
   * 超时自动返回的阻塞性的获取锁，不响应中断
   * @param time
   * @param unit
   * @return 若成功获取到锁，返回true,若在指定时间内未获取到锁，返回false
   */
  boolean tryLock(long time, TimeUnit unit);
 
  
  /**
   * 超时自动返回的阻塞性的获取锁，响应中断
   * @param time
   * @param unit
   * @return 
   * @throws InterruptedException在尝试获取锁的当前线程被中断
   */
  boolean tryLockInterruptibly(long time, TimeUnit unit) throws InterruptedException;
    
   /**
   * 释放锁
   */
   void unlock();
}

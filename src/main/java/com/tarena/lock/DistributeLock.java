package com.tarena.lock;

public interface DistributeLock {

  /**
   * 默认持有锁的最长时间
   */
  final int DEFAULT_EXPIRE = 300 * 1000;
  
  
  /**
   * 默认获取锁的最长等待时间
   */
  final int DEFAULT_TIMEOUT = 10 * 1000;
  
  /**
   * 获取锁
   * @param category 锁区分（如表名）
   * @param value 锁（如主键）
   * @param expireMs 持有锁的最长时间
   * @param timeoutMs 获取锁的最长等待时间
   * @return 锁钥匙
   */
  String getLock(String category, String value, int exprieMs, int timeoutMs);
  
  /**
   * 获取锁（按默认超时时间配置）
   * @param category
   * @param value
   * @return 锁钥匙
   */
  String getLock(String category, String value);
  
  /**
   * 解锁
   * @param category
   * @param value
   * @param lockKey
   * @return
   */
  boolean releaseLock(String category, String value, String lockKey);
}

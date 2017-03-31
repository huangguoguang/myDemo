package com.tarena.redis.service;

import java.io.Serializable;

public interface IRedisService {
  /**
   * 取缓存为空时存缓存
   * @param cacheName
   * @param cacheKey
   * @param cacheValue
   */
  void putCacheValueIfNullForMap(String cacheName,String cacheKey,Object cacheValue);
  
  
  /**
   * 强制存缓存（可覆盖原缓存）
   * @param cacheName
   * @param cacheKey
   * @param cacheValue
   */
  void putCacheValueForMap(String cacheName,String cacheKey,Object cacheValue);
  
  
  /**
   * 获取缓存value
   * @param cacheName
   * @param cacheKey
   * @return
   */
  Object getCacheValueForMap(String cacheName,String cacheKey);
  
  
  /**
   * 存取缓存
   * @param cacheKey
   * @param cacheValue
   * @param time
   */
  void putCacheForString(final String cacheKey, final String cacheValue, final long time);
  
  
  /**
   * 获取缓存value
   * @param cacheKey
   * @return
   */
  Serializable getCacheForString(final String cacheKey);
}

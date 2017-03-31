package com.tarena.redis.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.tarena.redis.service.IRedisService;

public class RedisServiceImpl implements IRedisService {
  @Autowired
  private CacheManager cacheManager;

  private RedisTemplate<String, Object> redisTemplate;

  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 取缓存为空时存缓存
   * 
   * @param cacheName
   * @param cacheKey
   * @param cacheValue
   **/
  @Override
  public void putCacheValueIfNullForMap(String cacheName, String cacheKey,
      Object cacheValue) {
    Cache cache = cacheManager.getCache(cacheName);
    ValueWrapper value = cache.get(cacheKey);
    if (value == null || value.get() == null) {
      cache.put(cacheKey, cacheValue);
    }

  }

  /**
   * 强制存缓存(可覆盖原缓存)
   * 
   * @param cacheName
   * @param cacheKey
   * @param cacheValue
   **/
  @Override
  public void putCacheValueForMap(String cacheName, String cacheKey,
      Object cacheValue) {
    Cache cache = cacheManager.getCache(cacheName);
    cache.put(cacheKey, cacheValue);
  }

  @Override
  public Object getCacheValueForMap(String cacheName, String cacheKey) {
    Cache cache = cacheManager.getCache(cacheName);
    ValueWrapper value = cache.get(cacheKey);
    Object result ;
    if(value == null || value.get() == null) {
      result = null;
    } else {
      result = value.get();
    }
    return result;
  }

  //redis存String对象
  @Override
  public void putCacheForString(final String cacheKey, final String cacheValue, final long time) {
    this.redisTemplate.execute(new RedisCallback<Object>() {
      @Override
      public Object doInRedis(RedisConnection connection) throws DataAccessException {
        connection.set(serializeKey(cacheKey), serializeVal(cacheValue));
        connection.expire(serializeKey(cacheKey), time);
        return null;
      }
      
    });
  }

  /**
   * 序列化key
   * @param cacheKey
   * @return
   */
  protected byte[] serializeKey(Object cacheKey) {
    return this.redisTemplate.getStringSerializer().serialize(String.valueOf(cacheKey));
  }
  
  private byte[] serializeVal(Object cacheValue){
    @SuppressWarnings("unchecked")
    RedisSerializer<Object> defaultRedisSerializer = (RedisSerializer<Object>) redisTemplate.getDefaultSerializer();
    return defaultRedisSerializer.serialize(cacheValue);
  }

  @Override
  public Serializable getCacheForString(final String cacheKey) {
    return (Serializable) this.redisTemplate.execute(new RedisCallback<Object>() {
      @Override
      public Object doInRedis(RedisConnection connection)
          throws DataAccessException {
        Object val = null;
        byte[] bytes = connection.get(serializeKey(cacheKey));
        if(bytes != null) {
          val = deserializeVal(bytes);
        }
        return val;
      }
      
    });
  }
  
  //反序列化
  private Object deserializeVal(byte[] bytes){
    @SuppressWarnings("unchecked")
    RedisSerializer<Object> defaultRedisSerializer = (RedisSerializer<Object>) this.redisTemplate.getDefaultSerializer();
    return defaultRedisSerializer.deserialize(bytes);
  }
  
  @SuppressWarnings("unused")
  private String deserializeKey(byte[] bytes) {
    return this.redisTemplate.getStringSerializer().deserialize(bytes);
  }
}

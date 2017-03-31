package com.tarena.common.component.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.util.Assert;

public class RedisCache implements Cache{
  private final String name;
  private int expire;
  private RedisTemplate<String, Object> redisTemplate;

  
  
  public RedisCache(String name, int expire,
      RedisTemplate<String, Object> redisTemplate) {
    this.name = name;
    this.expire = expire;
    this.redisTemplate = redisTemplate;
    Assert.notNull(this.redisTemplate, "RedisTemplate isn't initialized!");
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Object getNativeCache() {
    return this.redisTemplate;
  }

  @Override
  public ValueWrapper get(Object key) {
    ValueWrapper wrapper = null;
    Object value = this.redisTemplate.opsForHash().get(name, String.valueOf(key));
    if(value != null) {
      wrapper = new SimpleValueWrapper(value);
    }  
    return wrapper;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(Object key, Class<T> type) {
    return (T) this.redisTemplate.opsForHash().get(name, String.valueOf(key));
  }

  @Override
  public void put(final Object key, final Object value) {
    this.redisTemplate.execute(new SessionCallback<Object>() {
      @SuppressWarnings("unchecked")
      @Override
      public <K,V>Object execute(RedisOperations<K, V> operations)
          throws DataAccessException {
        operations.multi();
        operations.opsForHash().put((K)name, String.valueOf(key), value);
        operations.expire((K)name, expire, TimeUnit.SECONDS);
        operations.exec();
        return null;
      }
    });
  }

  @Override
  public ValueWrapper putIfAbsent(final Object key, final Object value) {
    return this.redisTemplate.execute(new SessionCallback<ValueWrapper>() {
      @SuppressWarnings("unchecked")
      @Override
      public <K,V>ValueWrapper execute(RedisOperations<K,V> operations)
          throws DataAccessException {
        ValueWrapper wrapper = null;
        boolean isb = operations.opsForHash().putIfAbsent((K) name, String.valueOf(key), value);
        if(!isb){
          Object val = operations.opsForHash().get((K) name,String.valueOf(key));
          if(val != null){
            wrapper = new SimpleValueWrapper(val);
          }
        }
        return wrapper;
      }
    });
    
  }

  @Override
  public void evict(Object key) {
    this.redisTemplate.opsForHash().delete(name, String.valueOf(key));
  }

  @Override
  public void clear() {
    this.redisTemplate.delete(name);
  }

}

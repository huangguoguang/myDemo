package com.tarena.lock;

import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author Administrator
 * 基于Redis的分布式锁的实现
 */
public class RedisBasedDistributedLock implements DistributeLock {
  private Logger logger = LoggerFactory.getLogger(RedisBasedDistributedLock.class);
  
  private RedisTemplate<String, String> redisTemplate;

  public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  private static DefaultRedisScript<Long> releaseLockScript = null;
  
  static {
    releaseLockScript = new DefaultRedisScript<>();
    releaseLockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/lua/checkAndDel.lua")));
    releaseLockScript.setResultType(Long.class);
  }
  
  @Override
  public String getLock(final String category, final String value, final int exprieMs,
      final int timeoutMs) {
    String lockVal = redisTemplate.execute(new RedisCallback<String>() {
      @Override
      public String doInRedis(RedisConnection connection)
          throws DataAccessException {
        String result = null;
        int timeOut = timeoutMs;
        int step = 100;
        
        String hval = UUID.randomUUID().toString();
        while(timeOut > 0) {
          boolean rst = connection.setNX(serializer(makeKey(category,value)), serializer(hval));
          if(rst){
            connection.pExpire(serializer(makeKey(category, value)), exprieMs);
            result = hval;
            break;
          }
          timeOut -= step;
          try {
            Thread.sleep(step);
          } catch (InterruptedException e) {
            logger.error("Interrupted when getting lock!", e);
          }
        }
        return result;
      }
    });
    return lockVal;
  }

  protected String makeKey(String category, String value) {
    return "lock:" + category + "_" + value;
  }

  protected byte[] serializer(String value) {
    return redisTemplate.getStringSerializer().serialize(value);
  }

  @Override
  public String getLock(String category, String value) {
    return getLock(category, value, DEFAULT_EXPIRE, DEFAULT_TIMEOUT);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean releaseLock(String category, String value, String lockKey) {
    System.out.println(lockKey);
    System.out.println(releaseLockScript.getScriptAsString());
    System.out.println(releaseLockScript.getSha1());
    return redisTemplate.execute(releaseLockScript, redisTemplate.getStringSerializer(), (RedisSerializer<Long>)redisTemplate.getDefaultSerializer(), Collections.singletonList(makeKey(category, value)), lockKey) == 1 ? true : false;
  }

}

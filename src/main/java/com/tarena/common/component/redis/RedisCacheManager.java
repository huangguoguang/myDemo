package com.tarena.common.component.redis;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import com.tarena.constants.CacheConf;

/**
 * redis缓存管理器
 * 
 * @author Administrator 超时时间支持XML配置和注解配置两种
 *
 */
public class RedisCacheManager extends
    AbstractTransactionSupportingCacheManager {
  private static Logger log = LoggerFactory.getLogger(RedisCacheManager.class);
  private final int DEFAULT_EXPIRED = 300;// 设置默认的超时时间
  private Map<String, Integer> expires = new HashMap<String, Integer>();// 缓存的时间
  private Map<String, Set<String>> cacheGroupMapping = new HashMap<>();// 缓存组
  private RedisTemplate<String, Object> redisTemplate;// redis模板
  private Class<?> configClass;// 配置类

  /**
   * RedisCacheManager在实例化完成以后会调用这个方法,这个方法做了什么?;
   *
   */
  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();// 调用了一个方法,返回了一个集合 并且把集合中的缓存添加到缓存中;
    getCacheConfig();
  }

  private void getCacheConfig() {
    if (configClass == null) {
      return;
    }
    Field[] fields = configClass.getDeclaredFields();
    for (Field field : fields) {
      if (Modifier.isStatic(field.getModifiers())
          && field.isAnnotationPresent(CacheConf.class)) {
        String cacheName = null;
        try {
          cacheName = field.get(null).toString();// 静态字段
        } catch (IllegalArgumentException | IllegalAccessException e) {
          log.error("获取注解缓存配置出错!", e);
          e.printStackTrace();
        }
        int expire = field.getAnnotation(CacheConf.class).expire();
        String group = field.getAnnotation(CacheConf.class).group();
        if (cacheGroupMapping.get(group) == null) {
          cacheGroupMapping.put(group, new HashSet<String>());
        }
        expires.put(cacheName, expire);
        cacheGroupMapping.get(group).add(cacheName);
        log.debug("[CACHE]加载缓存.. 缓存名:{} 超时时间:{} 缓存组：{} *****", new Object[]{cacheName,expire,group});
      }
    }
  }
  
  

  @Override
  protected Cache getMissingCache(String name) {
    Integer expire = expires.get(name);
    if(expire == null) {
      expire = DEFAULT_EXPIRED;
    }
    return new RedisCache(name, expire, redisTemplate);
  }

  @Override
  protected Collection<? extends Cache> loadCaches() {
    return new HashSet<RedisCache>();
  }

  public Map<String, Integer> getExpires() {
    return expires;
  }

  public void setExpires(Map<String, Integer> expires) {
    this.expires = expires;
  }

  public RedisTemplate<String, Object> getRedisTemplate() {
    return redisTemplate;
  }

  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public Map<String, Set<String>> getCacheGroupMapping() {
    return cacheGroupMapping;
  }

  public void setCacheGroupMapping(Map<String, Set<String>> cacheGroupMapping) {
    this.cacheGroupMapping = cacheGroupMapping;
  }

  public Class<?> getConfigClass() {
    return configClass;
  }

  public void setConfigClass(Class<?> configClass) {
    this.configClass = configClass;
  }

}

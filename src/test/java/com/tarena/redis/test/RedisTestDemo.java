package com.tarena.redis.test;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.tarena.common.component.session.SessionFactory;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.constants.CacheConstants;
import com.tarena.lock.DistributeLock;
import com.tarena.redis.service.IRedisService;

public class RedisTestDemo {

  private IRedisService iRedisService;
  
  private DistributeLock distributeLock;
  
  private SessionFactory sessionFactory;
  
  private MockHttpServletRequest req;
  
  private MockHttpServletResponse res;
  @Before
  public void init(){
    @SuppressWarnings("resource")
    ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"spring.xml","spring-redis.xml","spring-mongo.xml"});
    iRedisService = (IRedisService) ac.getBean("redisService");
    distributeLock = (DistributeLock) ac.getBean("distributedLock");
    sessionFactory = (SessionFactory) ac.getBean("sessionFactory");
    req = new MockHttpServletRequest();
    res = new MockHttpServletResponse(); 
  }
  
  @Test
  @Ignore
  public void testPutCacheValueIfNullForMap(){
    String cacheName = CacheConstants.CACHE_USER_WALLET;
    String cacheKey = "huang";
    String cacheValue = "guang";
    iRedisService.putCacheValueIfNullForMap(cacheName, cacheKey, cacheValue);
  }
  
  @Test
  @Ignore
  public void testPutCacheValueForMap(){
    String cacheName = CacheConstants.CACHE_USER_WALLET;
    String cacheKey = "zhang";
    String cacheValue = "guangguang";
    iRedisService.putCacheValueForMap(cacheName, cacheKey, cacheValue);
    
  }
  
  @Test
  @Ignore
  public void testGetCacheValueForMap(){
    String cacheName = CacheConstants.CACHE_USER_WALLET;
    String cacheKey = "zhangg";
    Object obj = iRedisService.getCacheValueForMap(cacheName, cacheKey);
    System.out.println(obj);
  }
  
  @Test
  @Ignore
  public void testPutCacheForString(){
    String cacheKey = "zhao";
    String cacheValue = "wu";
    long time = 10000;
    iRedisService.putCacheForString(cacheKey, cacheValue, time);
  }
  
  @Test
  @Ignore
  public void testGetCacheForString(){
    String cacheKey = "zhao";
    Object obj =  iRedisService.getCacheForString(cacheKey);
    System.out.println(obj);
  }
  
  @Test
  @Ignore
  public void testLock(){
    String lock1 = distributeLock.getLock("user", "huang", 10000, 5000);
    System.out.println("lock1:"+lock1);
    String lock2 = distributeLock.getLock("user", "huang", 10000, 5000);
    System.out.println("lock2:"+lock2);
    String lock3 = distributeLock.getLock("user", "huang", 10000, 5000);
    System.out.println("lock3:"+lock3);
  }
  
  @Test
  public void testSession(){
    @SuppressWarnings("unused")
    SessionProvider sp = sessionFactory.getSession(req, res);
    Cookie[] cookies = req.getCookies();
    for(Cookie cookie : cookies) {
      System.out.println(cookie.getPath());
    }
  }
}

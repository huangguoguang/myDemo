package com.tarena.constants;

/**
 * 缓存常量定义
 * @author Administrator
 *
 *
 */
public class CacheConstants {
  public final static String CACHE_GROUP_WALLET = "wallet";
  
  @CacheConf(expire = 5000, group = CACHE_GROUP_WALLET)
  public final static String CACHE_USER_WALLET = "user_wallet";
}

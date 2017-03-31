package com.tarena.redis.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.tarena.constants.CacheConf;
import com.tarena.constants.CacheConstants;

public class TestField {
  public static void main(String[] args) {
    //User user =  new User(1, "zs", 22, "abc");
    Field[] fields = CacheConstants.class.getDeclaredFields();
    System.out.println(fields.length);
    for (Field field : fields) {
      System.out.println(field.toString());
      System.out.println(Modifier.isStatic(field.getModifiers()));
      System.out.println(field.isAnnotationPresent(CacheConf.class));
      if(Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(CacheConf.class)){
        try {
          String cacheName = field.get(null).toString();
          int expire = field.getAnnotation(CacheConf.class).expire();
          String group = field.getAnnotation(CacheConf.class).group();
          System.out.println(cacheName);
          System.out.println(expire);
          System.out.println(group);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

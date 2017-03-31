package com.tarena.redis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class RedisDemo {
  public static void main(String[] args) {
    // 连接本地的 Redis 服务
    Jedis jedis = new Jedis("localhost");
    System.out.println("Connection to server sucessfully");
    // 查看服务是否运行
    System.out.println("Server is running: " + jedis.ping());

    stringDemo(jedis);
    listDemo(jedis);
    keysDemo(jedis);
  }

  //获取所有的key
  private static void keysDemo(Jedis jedis) {
    Set<String> keys = jedis.keys("*");
    Iterator<String> it = keys.iterator();
    int i = 1;
    while(it.hasNext()){
      System.out.println("key"+i + "****" +it.next());
      i++;
    }
    
  }

  // Redis Java List(列表) 实例
  private static void listDemo(Jedis jedis) {
    jedis.del("list");
    // 存储数据到列表中
    jedis.lpush("list", "zhangsan");
    jedis.lpush("list", "李四");
    jedis.lpush("list", "aaaa");
    // 获取存储的数据并输出
    List<String> list = jedis.lrange("list", 0, 10);
    System.out.println(list.size());
    for (int i = 0; i < list.size(); i++) {
      System.out.println("Stored string in redis:: " + list.get(i));
    }
  }

  // Redis Java String(字符串) 实例
  private static void stringDemo(Jedis jedis) {
    // 设置 redis 字符串数据
    jedis.set("mykey", "string value");
    // 获取存储的数据并输出
    System.out.println("Stored string in redis:: " + jedis.get("mykey"));
  }

}

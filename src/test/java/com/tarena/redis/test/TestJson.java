package com.tarena.redis.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.tarena.baseTest.SpringTestCase;

public class TestJson extends SpringTestCase{
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  @Test
  public void test(){
    String gzp = stringRedisTemplate.opsForValue().get("34_gzp");
    System.out.println(gzp);
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append("'type':");
    sb.append("'1','list':[{'type':{'code':'6','name':'zhang'},{'list':[{'id':'aaa','name':'aname'},{'id':'bbb','name':'bname'}]}]}");
    String str = sb.toString();
    str = str.replaceAll("'", "\"");
    System.out.println(str);
    System.out.println(JSONObject.parse(gzp));
  }
} 

package com.tarena.redis.test;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tarena.baseTest.SpringTestCase;

public class TestRedis extends SpringTestCase{

  @Autowired
  StringRedisTemplate stringRedisTemplate;
  
  @Test
  @Ignore
  public void testEntrances(){
    String entrances = stringRedisTemplate.opsForValue().get("entrances");
    JSONArray array = (JSONArray) JSONArray.parse(entrances);
    for(int i = 0 ;i < array.size();i++){
      JSONObject obj = array.getJSONObject(i);
      String agent_code = obj.getString("agent_code");
      if(agent_code.contains("#zyhj#")){
        System.out.println("pay_url"+obj.getString("url"));
      }else{
        System.out.println("http://mp.weixin.qq.com/s?__biz=MzI2MDUxODQ4OQ==&mid=100000006&idx=1&sn=fca9c5eff106ca2c9ada4d944c412dc5&chksm=6a6926305d1eaf26a36d6830359250315beb50ef3368aa4c46bbc203a66bd0d83f7bdfa2ad41#rd");
      }
      
    }
  }
  
  @Test
  public void testGetBuyPoint(){
    String buyType = "buy";
    String itemType = "mo13";
    String text = stringRedisTemplate.opsForValue().get(itemType);
    double currentPoint = Double.valueOf(JSONObject.parseObject(text).get("ws_new_point").toString());

    if ("mo13".equals(itemType)) {
      String use_key = stringRedisTemplate.opsForValue().get("use_key");
      if (StringUtils.isNotBlank(use_key)) {
        JSONArray jsonArray = JSONArray.parseArray(stringRedisTemplate.opsForValue().get(use_key));
        for (int i = 0; i < jsonArray.size(); i++) {
          double point = Double.valueOf(jsonArray.get(i).toString());
          if ("buy".equals(buyType)) {
            currentPoint = currentPoint > point ? currentPoint : point;
          } else {
            currentPoint = currentPoint > point ? point : currentPoint;
          }
        }
      }
    }
    System.out.println("当前点位是："+currentPoint);
  }
}

package com.tarena.redis.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TestFastJson {
  public static void main(String[] args) {
    /*{"key1":"One","key2":"Two"}
           将List<Map>转成JSON。*/
/*    Map<String,Object> map = new HashMap<String, Object>();
    map.put("key1", "one");
    map.put("key2", "two");
    String mapJson = JSON.toJSONString(map);
    System.out.println(mapJson);*/
    
    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    Map<String,Object> map1 = new HashMap<String, Object>();
    map1.put("key1","one" );
    map1.put("key2", "two");
    
    Map<String,Object> map2 = new HashMap<String, Object>();
    map2.put("key3", "three");
    map2.put("key4", "four");
    list.add(map1);
    list.add(map2);
    String listJson = JSON.toJSONString(list);
    System.out.println(listJson);
    
    JSONArray array = new JSONArray();
    array.add(JSONObject.parse(listJson));
    System.out.println(array);
  }
}

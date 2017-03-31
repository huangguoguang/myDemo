package com.tarena.mabits.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarena.baseTest.SpringTestCase;
import com.tarena.service.IUserService;

public class TestUser extends SpringTestCase{

  @Autowired
  private IUserService iUserService;
  @Test
  public void test(){
   List<Map<String, Object>> list =  iUserService.findItemImage();
   for(Map<String,Object> map : list){
     System.out.println(map.get("goods_code"));
     System.out.println(map.get("goods_url"));
   }
  }
}

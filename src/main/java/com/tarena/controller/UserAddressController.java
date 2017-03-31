package com.tarena.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tarena.common.component.session.SessionProvider;
import com.tarena.common.component.session.support.Session;
import com.tarena.entity.UserInfo;
import com.tarena.service.IUserAddrService;

@Controller
@RequestMapping("/addr")
public class UserAddressController extends BaseController {

  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private IUserAddrService addrService;
  
  @RequestMapping("/getDefaultAddr")
  @ResponseBody
  public Object getDefaultAddr(@Session(create = false)SessionProvider session,
      @RequestParam Map<String,Object> map){
    //获取用户信息
    UserInfo userInfo = (UserInfo)session.getAttribute(UserInfo.sessionKey);
    Map<String,Object> result = new HashMap<String, Object>();
    
    Map<String,Object> addMap = addrService.getDefaultAddr(userInfo);
    if(addMap == null){
      result.put("success",false);
      result.put("msg", "获取失败");
      return result;
    }
    result.put("success", true);
    result.put("msg", "获取正常");
    result.put("data", addMap);
    return result;
  }
}

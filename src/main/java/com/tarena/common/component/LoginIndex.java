package com.tarena.common.component;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.entity.UserInfo;
import com.tarena.service.IUserService;

@Component
public class LoginIndex {
  @Autowired
  private IUserService iUserService;
  
  private static Logger logger = Logger.getLogger(LoginIndex.class);
  public String loginIndex(SessionProvider session,String agent_code,String code){
    @SuppressWarnings("rawtypes")
    Map agentCompanyMap = iUserService.queryAgentInfoByAgentCode(agent_code);
    logger.info("AppID:" + agentCompanyMap.get("dept_app_id"));
    logger.info("AppSecret:" + agentCompanyMap.get("dept_app_secret"));
    logger.info("DeptUrl:" + agentCompanyMap.get("dept_url"));
    JSONObject client;
    @SuppressWarnings("unused")
    String url = "http%3a%2f%2f1ew4814073.imwork.net%2fuser%2fuserLoginPage";
    client = JSONObject.parseObject(""
        + "{"
        + "\"openid\":\"oDzJxwpCbmBpuI1yGCY-LmiiGn40\","
        + "\"nickname\":\"陈威\","
        + "\"sex\":1,"
        + "\"headimgurl\":\"http://wx.qlogo.cn/mmopen/cGNScwnYvk5mrEjaGpCb5orG82BaU6gsOicrSiaEw3lQIbgia1ShUIsiaPlbiciaiaPds5eespu6CREicdz5nTqdHQwukw/0\","
        + "\"privilege\":[]"
        + "}");
    logger.info("微信回复:" + client);
    UserInfo userInfo = new UserInfo();
    userInfo.setAgent_code(agent_code);
    userInfo.setUser_wxid(client.getString("openid"));
    userInfo.setUser_name(client.getString("nickname"));
    userInfo.setHead_url(client.getString("headimgurl"));
    session.setAttribute(UserInfo.sessionKeyLogin, userInfo);
    session.setAttribute("headimgurl", client.getString("headimgurl"));
    return "ok";
  }
}

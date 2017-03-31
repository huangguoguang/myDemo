package com.tarena.util;

import java.util.HashMap;
import java.util.Map;

import com.tarena.common.component.session.SessionProvider;

public class CheckLoginCountUtil {

  public static Map<String, Object> checkNeedMsgCode(SessionProvider session) {
    Map<String,Object> map = new HashMap<String, Object>();
    if(session.getAttribute("login_count") == null) {
      //表示是第一次登录，记录count
      int login_count = -1;
      session.setAttribute("login_count", login_count +1);
      map.put("login_count",login_count+1);
      map.put("need_code", false);
      return map;//不需要验证码
    } else {
      int count = (int) session.getAttribute("login_count");
      session.setAttribute("login_count", count + 1);
      if(count > 2) {
        map.put("login_count", count + 1);
        map.put("need_code", true);
        return map;
      } else {
        map.put("login_count", count + 1);
        map.put("need_code", false);
        return map;
      }
    }
  }

}

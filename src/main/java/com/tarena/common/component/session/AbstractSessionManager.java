package com.tarena.common.component.session;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * session管理抽象类
 * @author Administrator
 *
 */
public abstract class AbstractSessionManager implements SessionManager {

  @Override
  public SessionProvider getSession(HttpServletRequest req,
      HttpServletResponse res) {
    return getSession(req, res, true);
  }

  @Override
  public abstract SessionProvider getSession(HttpServletRequest req,
      HttpServletResponse res, boolean create);
  
  public String getSessionIdFromCookie(HttpServletRequest req){
    Set<String> s = new HashSet<String>();
    if(req.getCookies() != null) {
      for(Cookie c : req.getCookies()){
        if(COOKIE_NAME_SESSION_ID.equals(c.getName())){
          s.add(c.getValue());
        }
      }
    }
    if(s.size() == 0 || s.size() > 1){
      return null;
    }
    return s.iterator().next();
  }
  
  public void addSessionCookie(String sessionId,HttpServletRequest req,HttpServletResponse res){
    Cookie cookie = new Cookie(COOKIE_NAME_SESSION_ID, sessionId);
    cookie.setPath(StringUtils.isBlank(req.getContextPath()) ? "/" :req.getContextPath()+"/");
    cookie.setHttpOnly(true);
    res.addCookie(cookie);
  }
}

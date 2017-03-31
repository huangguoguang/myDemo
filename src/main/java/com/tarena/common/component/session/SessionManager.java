package com.tarena.common.component.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SessionManager {
  final static String COOKIE_NAME_SESSION_ID = "UID";
  
  /**
   * 取得session 默认强制创建
   * @param req
   * @param res
   * @return
   */
  public SessionProvider getSession(HttpServletRequest req,HttpServletResponse res);
  
  /**
   * 取得session
   * @param req
   * @param res
   * @param create 是否强制创建
   * @return
   */
  public SessionProvider getSession(HttpServletRequest req,HttpServletResponse res,boolean create);
}

package com.tarena.common.component.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * session工厂
 * @author Administrator
 *
 */
public class SessionFactory {
  private SessionManager sessionManager;

  public void setSessionManager(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }
  
  /**
   * 取得sessionProvider对象，不存在则创建一个SessionProvider
   * @param req
   * @param res
   * @return
   */
  public SessionProvider getSession(HttpServletRequest req,HttpServletResponse res){
    return sessionManager.getSession(req, res, true);
  }
  
  /**
   * @param req
   * @param res
   * @param create是否强制创建
   * @return
   */
  public SessionProvider getSession(HttpServletRequest req,HttpServletResponse res,boolean create){
    return sessionManager.getSession(req, res, create);
  }
  
}

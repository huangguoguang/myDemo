package com.tarena.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tarena.entity.CallBackMsg;

public class BaseController {
  public CallBackMsg msg = new CallBackMsg();
  public int pageNum;
  public int pageSize = 10;
  private Object jsonObject;
  public static final String JSON_STRING = "json",
                             JSON_ARRAY="json",
                             JSON_OBJECT="json",
                             JSON_RESULT="json",
                             JSON_EXCEPTION = "json";
  
  
  /**
   * 获取request
   * @return
   */
  public HttpServletRequest getRequest(){
    return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
  }
  
  /**
   * 获取session
   * @return
   */
  public HttpSession getSession(){
    return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
  }
}

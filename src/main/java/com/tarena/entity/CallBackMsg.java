package com.tarena.entity;

import java.io.Serializable;

public class CallBackMsg implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private boolean success = false;
  private String msg;//回调信息
  private String code;
  private int login_count;//登录次数
  private boolean need_username;
  public boolean isSuccess() {
    return success;
  }
  public void setSuccess(boolean success) {
    this.success = success;
  }
  public String getMsg() {
    return msg;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }
  public String getCode() {
    return code;
  }
  public void setCode(String code) {
    this.code = code;
  }
  public int getLogin_count() {
    return login_count;
  }
  public void setLogin_count(int login_count) {
    this.login_count = login_count;
  }
  public boolean isNeed_username() {
    return need_username;
  }
  public void setNeed_username(boolean need_username) {
    this.need_username = need_username;
  }
  

}

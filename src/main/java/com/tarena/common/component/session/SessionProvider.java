package com.tarena.common.component.session;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * @author Administrator
 * 为实现分布式、集中式会话服务器（session server），统一会话接口
 */
public interface SessionProvider {
  /**
   * 会话id
   * @return
   */
  public String getId();
  
  /**
   * 创建会话时间
   * @return
   */
  public long getCreationTime();
  
  /**
   * 会话最后访问时间
   * @return
   */
  public long getLastAccessedTime();
  
  /**
   * 取得会话属性
   * @param name
   * @return
   */
  public Serializable getAttribute(String name);
  
  
  /**
   * 取得会话属性名集合
   * @return
   */
  public Enumeration<String> getAttributeNames();
  /**
   * 设置会话属性
   * @param name
   * @param value
   */
  public void setAttribute(String name,Serializable value);
  /**
   * 移除会话属性
   * @param name
   */
  public void removeAttribute(String name);
  
  /**
   * 销毁会话
   */
  public void invalidate();
}

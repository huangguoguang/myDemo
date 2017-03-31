package com.tarena.exception;
/** 
 * @author: chenwei
 * @version：1.0 
 * @创建时间：2016年8月11日 下午7:34:18 
 * @说明：用户登录异常
 *
 */
public class UserModifyPasswordException extends Exception {
	
	private static final long serialVersionUID = 8867406779244493619L;

	public UserModifyPasswordException(String msg)  
  {  
      super(msg);  
  }  
}

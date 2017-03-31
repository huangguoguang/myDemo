package com.tarena.exception;
/** 
 * @author: chenwei
 * @version：1.0 
 * @创建时间：2016年8月11日 下午7:34:18 
 * @说明：用户登录异常
 *
 */
public class UserLoginException extends Exception {
	
	private static final long serialVersionUID = 6008001344006236366L;

	public UserLoginException(String msg)  
  {  
      super(msg);  
  }  
}

package com.tarena.exception;
/** 
 * @author: chenwei
 * @version：1.0 
 * @创建时间：2016年8月8日 下午5:41:53 
 * @说明：新用户注册异常
 *
 */
public class UserRegisterException extends Exception {
	
	private static final long serialVersionUID = 6008001344006236366L;

	public UserRegisterException(String msg)  
  {  
      super(msg);  
  }  
}

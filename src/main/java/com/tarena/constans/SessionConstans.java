/**
 * Project Name: oa_sp
 * File Name: SessionConstans.java
 * Package Name: com.anchol.oa.sp.constants
 * Date: 2014年10月10日上午12:04:59
 * 
 * © 2014 武汉东升阳科技有限公司版权所有
 */
package com.tarena.constans;

import com.tarena.common.component.session.support.ServletsWrapper;
import com.tarena.util.Servlets;


/**
 * Class Name: SessionConstans<br>
 * Description: Session会话常量
 *
 * @author LiLin
 * @version 1.0
 */
public class SessionConstans {
	
	// 验证码
	public final static String KEY_CAPTCHA_TOKEN = "s_captchaToken";
	// 手机短信验证码
	public final static String KEY_PHONE_CAPTCHA_TOKEN = "s_phoneCaptchaToken";
	
	// 用户id
	public final static String KEY_USER_ID = "s_userId";
	
	// 经纪人id
	public final static String KEY_BROKER_ID = "s_brokerId";
	
	// 后台用户id
	public final static String KEY_SYS_USER_ID = "s_sysUserId";
	
	// 访问令牌
	public final static String KEY_TOKEN = ServletsWrapper.SESSION_KEY_TOKEN;
	
	// 某一期间内（比如5秒之内）首次访问的时间戳
	public final static String KEY_ACCESS_FIRST_TIMESTAMP_DURING_PERIOD = Servlets.SESSION_KEY_ACCESS_FIRST_TIMESTAMP;
	
	// 某一期间内（比如5秒之内）的总共访问次数
	public final static String KEY_ACCESS_TIMES = Servlets.SESSION_KEY_ACCESS_TIMES;
	
}

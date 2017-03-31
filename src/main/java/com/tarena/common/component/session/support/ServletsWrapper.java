/**
 * 
 */
package com.tarena.common.component.session.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tarena.common.component.session.SessionFactory;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.common.spring.SpringContextHolder;
import com.tarena.util.Servlets;

/**
 * Class Name: ServletsWrapper<br>
 * Description: 
 *
 * @author lilin
 * @version 1.0
 */
public class ServletsWrapper extends Servlets {
	// 令牌头
	public static final String RESPONSE_HEADER_TOKEN = "token";
	public static final String SESSION_KEY_TOKEN = "_token";

	private ServletsWrapper() {
		
	}
	
	/**
	 * 设置令牌头
	 * @param response
	 */
	public static void setTokenHeader(HttpServletRequest request, HttpServletResponse response){
		SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
		SessionProvider session = sessionFactory.getSession(request, response, false);
		if(session == null){
			return;
		}
		response.setHeader(RESPONSE_HEADER_TOKEN, (String)session.getAttribute(SESSION_KEY_TOKEN));
	}
	
}

/**
 * 
 */
package com.tarena.common.component.session.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.tarena.common.component.session.SessionFactory;
import com.tarena.common.component.session.SessionProvider;

/**
 * Class Name: SessionProviderArgumentResolver<br>
 * Description: SessionProvider参数解析<br>
 * 提供SpringMVC风格的参数[SessionProvider]植入
 *
 * @author lilin
 * @version 1.0
 */
public class SessionProviderArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Session.class) && parameter.getParameterType().equals(SessionProvider.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Session sessionAnnotation = parameter.getParameterAnnotation(Session.class);
		SessionProvider session = sessionFactory.getSession(webRequest.getNativeRequest(HttpServletRequest.class), webRequest.getNativeResponse(HttpServletResponse.class), sessionAnnotation.create());
		return session;
	}

}

/**
 * 
 */
package com.tarena.common.component.redis;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import com.tarena.common.component.session.AbstractSessionManager;
import com.tarena.common.component.session.RedisSessionHelper;
import com.tarena.common.component.session.SessionProvider;

/**
 * Class Name: RedisSessionManager<br>
 * Description: 会话管理器
 *
 * @author lilin
 * @version 1.0
 */
public class RedisSessionManager extends AbstractSessionManager implements InitializingBean {

	private RedisTemplate<String, Serializable> redisTemplate;

	private String sessionCacheName;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(redisTemplate);
		Assert.notNull(sessionCacheName);
	}

	
	@Override
	public SessionProvider getSession(HttpServletRequest req, HttpServletResponse res, boolean create) {
		SessionProvider session = null;
		String id = getSessionIdFromCookie(req);//根据uid来取得sessionid如果没有sessionid

		if (id != null && contains(id)) {//如果找得到cookie中的sessionId
			session = new RedisSessionProvider(redisTemplate, sessionCacheName, 5*60, id);//刷新了一条redis记录的失效时间
			addSessionCookie(session.getId(), req, res);
		} else if (create) {//如果是第一次进来并且需要创建session
			session = new RedisSessionProvider(redisTemplate, sessionCacheName, 5*60);//这个构造方法很关键
			addSessionCookie(session.getId(), req, res);
		}
		return session;
	}

	/**
	 * SessionId是否存在
	 * 
	 * @param sessionId
	 * @return
	 */
	public boolean contains(String sessionId) {
		return redisTemplate.hasKey(RedisSessionHelper.getCacheKey(sessionCacheName, sessionId));
	}

	public void setRedisTemplate(RedisTemplate<String, Serializable> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	public void setSessionCacheName(String sessionCacheName) {
		this.sessionCacheName = sessionCacheName;
	}

}

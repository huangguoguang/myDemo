/**
 * 
 */
package com.tarena.common.component.redis;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.tarena.common.component.session.RedisSessionHelper;
import com.tarena.common.component.session.SessionIdProvider;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.common.component.session.UUIDProvider;

/**
 * Class Name: RedisSessionProvider<br>
 * Description: session的Redis实现<br>
 *
 * @author lilin
 * @version 1.0
 */
public class RedisSessionProvider implements SessionProvider {
	private final static String SESSION_CREATION_TIME = "@_creation_time";
	private final static String SESSION_LAST_ACCESSED_TIME = "@_last_accessed_time";

	private RedisTemplate<String, Serializable> template;
	private String id;//随机产生的id
	private String key;//由cacheName：id组成的字符串
	private String cacheName;//调用RedisSessionProvider方法传递进来的sessionCacheName
	private int expire = 30 * 60;//默认超时时间
	private SessionIdProvider sessionIdProvider = new UUIDProvider();//里面只有个得到sessionId的方法

	
	//如果是第一次进来就调用这个构造方法
	public RedisSessionProvider(RedisTemplate<String, Serializable> redisTemplate, String sessionCacheName, int SessionTimeOut) {
		this.template = redisTemplate;
		this.cacheName = sessionCacheName;
		this.expire = SessionTimeOut * 60;//单位是秒
		this.id = sessionIdProvider.getSessionId();//返回随机产生的sessionid
		this.key = getKey();//传递id和cacheName，返回一个由cacheName：id组成的字符串
		initCacheSpace();
	}

	//如过不是第一次进来
	public RedisSessionProvider(RedisTemplate<String, Serializable> redisTemplate, String sessionCacheName, int SessionTimeOut, String sessionId) {
		this.template = redisTemplate;
		this.cacheName = sessionCacheName;
		this.expire = SessionTimeOut * 60;
		this.id = sessionId;
		this.key = getKey();
		setLastAccessTimeAndExpire();//设置最后访问时间及重设超时时间
	}

	private void initCacheSpace() {
		template.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				if (!connection.exists(serializeKey(key))) {//如果redis连接里面 没有这个key
					long creationTime = new Date().getTime();
					Map<byte[], byte[]> attributeMap = new HashMap<>();
					attributeMap.put(serializeKey(SESSION_CREATION_TIME), serializeVal(creationTime));//session创建的时间
					attributeMap.put(serializeKey(SESSION_LAST_ACCESSED_TIME), serializeVal(creationTime));//最后修改时间
					connection.hMSet(serializeKey(key), attributeMap);//这个key的map里面放了最后修改时间和创建的时间
					connection.expire(serializeKey(key), expire);//放入一个失效的时间
				}
				return null;
			}
		});
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public long getCreationTime() {
		return this.template.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				long time = 0;
				byte[] bytes = connection.hGet(serializeKey(key), serializeKey(SESSION_CREATION_TIME));
				if (bytes != null) {
					time = (Long) deserializeVal(bytes);
					setLastAccessTimeAndExpire(connection);
				}
				return time;
			}
		});
	}

	@Override
	public long getLastAccessedTime() {
		return this.template.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				long time = 0;
				byte[] bytes = connection.hGet(serializeKey(key), serializeKey(SESSION_LAST_ACCESSED_TIME));
				if (bytes != null) {
					time = (Long) deserializeVal(bytes);
					setLastAccessTimeAndExpire(connection);
				}
				return time;
			}
		});
	}

	@Override
	public Serializable getAttribute(final String name) {
		return (Serializable) this.template.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				Object val = null;
				byte[] bytes = connection.hGet(serializeKey(key), serializeKey(name));
				if (bytes != null) {
					val = (Object) deserializeVal(bytes);
					setLastAccessTimeAndExpire(connection);
				}
				return val;
			}
		});
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return this.template.execute(new RedisCallback<Enumeration<String>>() {
			public Enumeration<String> doInRedis(RedisConnection connection) throws DataAccessException {
				Enumeration<String> res = null;
				Set<byte[]> keySet = connection.hKeys(serializeKey(key));
				if (keySet != null && keySet.size() > 0) {
					Set<String> keyStrSet = new HashSet<>();
					Iterator<byte[]> itera = keySet.iterator();
					while (itera.hasNext()) {
						keyStrSet.add(deserializeKey(itera.next()));
					}
					Vector<String> v = new Vector<>(keyStrSet);
					res = v.elements();
					setLastAccessTimeAndExpire(connection);
				}
				return res;
			}
		});
	}

	@Override
	public void setAttribute(final String name, final Serializable value) {//模拟session的setAttribute方法
		this.template.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.hSet(serializeKey(key), serializeKey(name), serializeVal(value));//serializeVal序列化一个Object
				setLastAccessTimeAndExpire(connection);//在每次操作以后重置时间
				return null;
			}
		});
	}

	@Override
	public void removeAttribute(final String name) {
		this.template.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.hDel(serializeKey(key), serializeKey(name));
				setLastAccessTimeAndExpire(connection);
				return null;
			}
		});

	}

	@Override
	public void invalidate() {
		this.template.delete(key);
	}

	/**
	 * session通用操作，每次操作后设置最后访问时间及重设超时时间
	 */
	private void setLastAccessTimeAndExpire() {
		this.template.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				setLastAccessTimeAndExpire(connection);
				return null;
			}
		});
	}

	/**
	 * session通用操作，每次操作后设置最后访问时间及重设超时时间
	 * 
	 * @param connection
	 */
	private void setLastAccessTimeAndExpire(RedisConnection connection) {
		if (connection.exists(serializeKey(key))) {
			connection.hSet(serializeKey(key), serializeKey(SESSION_LAST_ACCESSED_TIME), serializeVal(new Date().getTime()));
			connection.expire(serializeKey(key), expire);
		}
	}

	/**
	 * 便捷序列化Key
	 * 
	 * @param obj
	 * @return
	 */
	private byte[] serializeKey(Object obj) {
		return template.getStringSerializer().serialize(String.valueOf(obj));
	}

	/**
	 * 便捷序列化Value
	 * 
	 * @param obj
	 * @return
	 */
	private byte[] serializeVal(Object obj) {
		@SuppressWarnings("unchecked")
		RedisSerializer<Object> defaultRedisSerializer = (RedisSerializer<Object>) template.getDefaultSerializer();
		return defaultRedisSerializer.serialize(obj);
	}

	/**
	 * 便捷反序列化Key
	 * 
	 * @param obj
	 * @return
	 */
	private String deserializeKey(byte[] bytes) {
		return template.getStringSerializer().deserialize(bytes);
	}

	/**
	 * 便捷反序列化Value
	 * 
	 * @param obj
	 * @return
	 */
	private <T> Object deserializeVal(byte[] bytes) {
		@SuppressWarnings("unchecked")
		RedisSerializer<Object> defaultRedisSerializer = (RedisSerializer<Object>) template.getDefaultSerializer();
		return defaultRedisSerializer.deserialize(bytes);
	}

	/**
	 * 取得缓存key
	 * 
	 * @return
	 */
	private String getKey() {
		return RedisSessionHelper.getCacheKey(cacheName, id);
	}

}

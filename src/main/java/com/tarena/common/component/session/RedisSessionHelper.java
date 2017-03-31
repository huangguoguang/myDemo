/**
 * 
 */
package com.tarena.common.component.session;

/**
 * Class Name: RedisSessionHelper<br>
 * Description: 
 *
 * @author lilin
 * @version 1.0
 */
public class RedisSessionHelper {

	public static String getCacheKey(String cacheName, String sessionId){
		return cacheName + ":" + sessionId;
	}
}

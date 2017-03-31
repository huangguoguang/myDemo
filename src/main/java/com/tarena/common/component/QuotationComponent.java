package com.tarena.common.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class QuotationComponent {

	@Autowired
	private StringRedisTemplate redisTemplate;

	/**
	 * 取建仓点位
	 * 
	 * 
	 * */
	public double getBuyPoint(String itemType, String buyType) {
		String text = redisTemplate.opsForValue().get(itemType);
		double currentPoint = Double.valueOf(JSONObject.parseObject(text).get("ws_new_point").toString());

		if ("mo13".equals(itemType)) {
			String use_key = redisTemplate.opsForValue().get("use_key");
			if (StringUtils.isNotBlank(use_key)) {
				JSONArray jsonArray = JSONArray.parseArray(redisTemplate.opsForValue().get(use_key));
				for (int i = 0; i < jsonArray.size(); i++) {
					double point = Double.valueOf(jsonArray.get(i).toString());
					if ("buy".equals(buyType)) {
						currentPoint = currentPoint > point ? currentPoint : point;
					} else {
						currentPoint = currentPoint > point ? point : currentPoint;
					}
				}
			}
		}
		return currentPoint;
	}

	/**
	 * 取平仓点位
	 * 
	 * */
	public double getSellPoint(String itemType, String buyType) {
		String text = redisTemplate.opsForValue().get(itemType);
		double currentPoint = Double.valueOf(JSONObject.parseObject(text).get("ws_new_point").toString());
		return currentPoint;
	}
}

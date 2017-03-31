/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.tarena.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Http与Servlet工具类.
 * 使用ServletsWrapper代替
 * 
 * @author calvin
 */
public class Servlets {

	// -- Content Type 定义 --//
	public static final String EXCEL_TYPE = "application/vnd.ms-excel";
	public static final String HTML_TYPE = "text/html";
	public static final String JS_TYPE = "text/javascript";
	public static final String JSON_TYPE = "application/json";
	public static final String XML_TYPE = "text/xml";
	public static final String TEXT_TYPE = "text/plain";
	public static final String PDF_TYPE = "application/pdf";
	
	// -- 常用数值定义 --//
	public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;
	
	// -- Header 定义 --
	public static final String RESPONSE_HEADER_SESSION_TIMEOUT = "session-timeout";
	public static final String RESPONSE_HEADER_ENCOUNTEREXCEPTION = "encounter-exception";
	public static final String RESPONSE_HEADER_ACCESSCONTROL = "ac-deny";
	// 某一期间内（比如5秒之内）首次访问的时间戳
	public final static String SESSION_KEY_ACCESS_FIRST_TIMESTAMP = "_access_first_timestamp";
	// 某一期间内（比如5秒之内）的总共访问次数
	public final static String SESSION_KEY_ACCESS_TIMES = "_access_times";
	
	protected Servlets() {
	}

	/**
	 * 设置客户端缓存过期时间 的Header.
	 */
	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
		// Http 1.0 header, set a fix expires date.
		response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000);
		// Http 1.1 header, set a time after now.
		response.setHeader("Cache-Control", "private, max-age=" + expiresSeconds);
	}

	/**
	 * 设置禁止客户端缓存的Header.
	 */
	public static void setNoCacheHeader(HttpServletResponse response) {
		// Http 1.0 header
		response.setDateHeader("Expires", 1L);
		response.addHeader("Pragma", "no-cache");
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}

	/**
	 * 设置LastModified Header.
	 */
	public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
		response.setDateHeader("Last-Modified", lastModifiedDate);
	}

	/**
	 * 设置Etag Header.
	 */
	public static void setEtag(HttpServletResponse response, String etag) {
		response.setHeader("ETag", etag);
	}

	/**
	 * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
	 * 
	 * 如果无修改, checkIfModify返回false ,设置304 not modify status.
	 * 
	 * @param lastModified
	 *            内容的最后修改时间.
	 */
	public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
			long lastModified) {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		return true;
	}

	/**
	 * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
	 * 
	 * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
	 * 
	 * @param etag
	 *            内容的ETag.
	 */
	public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
		String headerValue = request.getHeader("If-None-Match");
		if (headerValue != null) {
			boolean conditionSatisfied = false;
			if (!"*".equals(headerValue)) {
				StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

				while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
					String currentToken = commaTokenizer.nextToken();
					if (currentToken.trim().equals(etag)) {
						conditionSatisfied = true;
					}
				}
			} else {
				conditionSatisfied = true;
			}

			if (conditionSatisfied) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", etag);
				return false;
			}
		}
		return true;
	}

	/**
	 * 设置让浏览器弹出下载对话框的Header.
	 * 
	 * @param fileName
	 *            下载后的文件名.
	 */
	public static void setFileDownloadHeader(HttpServletResponse response, String userAgent, String fileName) {
		try {
			// 中文文件名支持
			String encodedfileName = null;
			if (userAgent != null) {
				if (userAgent.indexOf("Firefox") != -1) {// Firefox
					encodedfileName = "=?UTF-8?B?"
							+ (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName
									.getBytes("UTF-8")))) + "?=";
				} else if (-1 != userAgent.indexOf("Chrome")) {// Chrome
					encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
				} else {// IE7+
					encodedfileName = java.net.URLEncoder.encode(fileName, "UTF-8");
					encodedfileName = encodedfileName.replace("+", "%20"); // 替换空格
				}
			} else {
				encodedfileName = fileName;
			}
			
			
			response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + "\"");
		} catch (UnsupportedEncodingException e) {
			
		}
	}

	/**
	 * 取得带相同前缀的Request Parameters, copy from spring.
	 * 
	 * 返回的结果的Parameter名已去除前缀.
	 * 
	 * 比如有m2
	 */
	public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
		Validate.notNull(request, "Request must not be null");
		Enumeration paramNames = request.getParameterNames();
		Map<String, Object> params = new TreeMap<String, Object>();
		if (prefix == null) {
			prefix = "";
		}
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ("".equals(prefix) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if (values == null || values.length == 0) {
					// Do nothing, no values found at all.
				} else if (values.length > 1) {
					params.put(unprefixed, values);
				} else {
					params.put(unprefixed, values[0]);
				}
			}
		}
		return params;
	}

	/**
	 * 客户端对Http Basic验证的 Header进行编码.
	 */
	public static String encodeHttpBasic(String userName, String password) {
		String encode = userName + ":" + password;
		return "Basic " + Encodes.encodeBase64(encode.getBytes());
	}

	/**
	 * 判断是否是ajax请求
	 * 
	 * @param req
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		return request.getHeader("x-requested-with") != null
				&& request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest");
	}
	
	/**
	 * 设置超时头
	 * @param response
	 */
	public static void setTimeoutHeader(HttpServletResponse response){
		response.setHeader(RESPONSE_HEADER_SESSION_TIMEOUT, "true");
	}
	
	/**
	 * 设置异常头
	 * @param response
	 */
	public static void setExceptionHeader(HttpServletResponse response){
		response.setHeader(RESPONSE_HEADER_ENCOUNTEREXCEPTION, "true");
	}
	
	/**
	 * 设置访问频率控制头
	 * @param request
	 * @param response
	 */
	public static void setAccessControlHeader(HttpServletResponse response){
		response.setHeader(RESPONSE_HEADER_ACCESSCONTROL, "true");
	}
	
	/**
	 * 获取访问网站的根url地址
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request){
		return request.getScheme() + "://" + request.getServerName()
				+ ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort())+request.getContextPath();
	}
	
	/**
	 * 获取客户端访问IP
	 * @param request
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
        //多次反向代理后会有多个ip值，第一个ip才是真实ip
        int index = ip.indexOf(",");
        if(index != -1){
            return ip.substring(0,index);
        }else{
            return ip;
        }
    }
    ip = request.getHeader("X-Real-IP");
    if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
        return ip;
    }
    return request.getRemoteAddr();
  }
}

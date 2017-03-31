package com.tarena.util;

import org.apache.log4j.Logger;

/**
 * <b>模块描述	: </b>LOG4J工具类<br/>
 * <b>使用描述	: </b><br/>
 * <b>JDK版本	: </b>JDK 1.6<br/>
 * <b>作者		: </b>辜家乐<br/>
 * <b>创建日期	: </b>2009-01-01<br/>
 * <b>创建版本	: </b>v1.0<br/>
 * ###############修改记录###############<br/>
 * …………v1.0<br/>
 * 修改日期	: 2009-01-01<br/>
 * 修改人		: 辜家乐<br/>
 * 修改原因	: -<br/>
 * 修改说明	: 初始建立<br/>
 * 代码备注	: -<br/>
 * ####################################<br/>
 * @author weili
 */
public final class LogUtils {

	private LogUtils() {
	}

	/**
	 * Logger实例
	 */
	private static Logger instance;

	/**
	 * 创建Logger实例的方法
	 * 
	 * @return Logger实例
	 */
	private static Logger getInstance() {
		if (instance == null) {
			instance = Logger.getLogger("CONSOLE");
		}
		return instance;
	}

	/**
	 * 系统正常日志
	 * 
	 * @param obj
	 *            需要显示的信息,为object类型
	 */
	public static void info(Object obj) {
		LogUtils.getInstance().info(obj);
	}

	/**
	 * 系统错误日志
	 * 
	 * @param obj
	 *            需要显示的信息,为object类型
	 */
	public static void error(Object obj) {
		LogUtils.getInstance().error(obj);
	}

	/**
	 * 系统调试信息
	 * 
	 * @param obj
	 *            需要显示的信息,为object类型
	 */
	public static void debug(Object obj) {
		LogUtils.getInstance().debug(obj);
	}
}

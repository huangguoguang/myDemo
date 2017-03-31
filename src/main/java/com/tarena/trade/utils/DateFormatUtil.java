package com.tarena.trade.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {

	public static String convertCurrentDate(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}

	public static void main(String[] args) {
		System.out.println(convertCurrentDate("yyyyMMddHHmmss"));
		System.out.println(convertCurrentDate("yyyy-MM-dd HH:mm:ss.SSS"));
	}

	public static Date addSecond(Date date, int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, second);
		return c.getTime();
	}

	static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

	public static String convertYYYYMMDDHHMMSS(Date date) {
		return yyyyMMddHHmmss.format(date);
	}

}

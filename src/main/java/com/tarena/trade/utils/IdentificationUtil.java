package com.tarena.trade.utils;

import java.util.Random;

import com.tarena.common.component.session.UUIDProvider;

public class IdentificationUtil {

	public static String generateId() {
//		return DateFormatUtil.convertCurrentDate("yyyyMMddHHmmssSSS") + new Random().nextInt(9999);
		return new UUIDProvider().getSessionId();
	}
	
	public static String generateLoginId() {
		return DateFormatUtil.convertCurrentDate("yyMMddHHmmss") + new Random().nextInt(999);
	}
	
	public static void main(String[] args) {
		System.out.println(new Random().nextInt(9999));
	}
}

package com.tarena.trade.enums;

public enum EnumsTradeDirection {
	BUY("buy", "买涨"), SELL("sell", "买跌");

	private String code;
	private String desc;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	EnumsTradeDirection(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	// 是否包含
	public static boolean container(String code) {
		for (EnumsTradeDirection tmp : EnumsTradeDirection.values()) {
			if (tmp.getCode().equals(code)) {
				return true;
			}
		}
		return false;
	}

	public boolean match(String code) {
		return this.code.equals(code);
	}

}

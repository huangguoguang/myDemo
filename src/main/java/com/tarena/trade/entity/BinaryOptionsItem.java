package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class BinaryOptionsItem implements Serializable {

	private static final long serialVersionUID = -7838764376120486702L;

	private String gdsy_id; // 主键id
	private String gdsy_name; // 商品名称
	private String gdsy_type; // 商品类型
	private double gdsy_ratio; // 收益比率
	private int gdsy_time; // 收益时间
	private double gdsy_poundage; // 商品手续费
	private double gdsy_money; // 商品金额
	private String is_use; // 是否可用
	private Date create_date; // 创建时间
	private int gdsy_order; // 收益排序
	private int gdsy_lot; // 商品最大手数
	private String gdsy_buy_time; // 商品可交易时间
	private String gdsy_buy_date; // 商品可交易日期

	public String getGdsy_id() {
		return gdsy_id;
	}

	public void setGdsy_id(String gdsy_id) {
		this.gdsy_id = gdsy_id;
	}

	public String getGdsy_name() {
		return gdsy_name;
	}

	public void setGdsy_name(String gdsy_name) {
		this.gdsy_name = gdsy_name;
	}

	public String getGdsy_type() {
		return gdsy_type;
	}

	public void setGdsy_type(String gdsy_type) {
		this.gdsy_type = gdsy_type;
	}

	public double getGdsy_ratio() {
		return gdsy_ratio;
	}

	public void setGdsy_ratio(double gdsy_ratio) {
		this.gdsy_ratio = gdsy_ratio;
	}

	public int getGdsy_time() {
		return gdsy_time;
	}

	public void setGdsy_time(int gdsy_time) {
		this.gdsy_time = gdsy_time;
	}

	public double getGdsy_poundage() {
		return gdsy_poundage;
	}

	public void setGdsy_poundage(double gdsy_poundage) {
		this.gdsy_poundage = gdsy_poundage;
	}

	public double getGdsy_money() {
		return gdsy_money;
	}

	public void setGdsy_money(double gdsy_money) {
		this.gdsy_money = gdsy_money;
	}

	public String getIs_use() {
		return is_use;
	}

	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public int getGdsy_order() {
		return gdsy_order;
	}

	public void setGdsy_order(int gdsy_order) {
		this.gdsy_order = gdsy_order;
	}

	public int getGdsy_lot() {
		return gdsy_lot;
	}

	public void setGdsy_lot(int gdsy_lot) {
		this.gdsy_lot = gdsy_lot;
	}

	public String getGdsy_buy_time() {
		return gdsy_buy_time;
	}

	public void setGdsy_buy_time(String gdsy_buy_time) {
		this.gdsy_buy_time = gdsy_buy_time;
	}

	public String getGdsy_buy_date() {
		return gdsy_buy_date;
	}

	public void setGdsy_buy_date(String gdsy_buy_date) {
		this.gdsy_buy_date = gdsy_buy_date;
	}

}

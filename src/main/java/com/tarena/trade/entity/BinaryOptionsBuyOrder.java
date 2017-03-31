package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class BinaryOptionsBuyOrder implements Serializable {

	private static final long serialVersionUID = -7512114569905192396L;

	private String gdsy_order_id; // 订单号

	private String agent_id; // 代理商ID
	private String deptment_id; // 部门ID
	private String broker_id; // 经纪人ID
	private String user_id; // 用户

	private String gdsy_buy_type; // 方向
	private double gdsy_buy_point; // 建仓点位
	private Date gdsy_buy_time; // 建仓时间
	private double gdsy_sell_point; // 建仓点位
	private Date gdsy_sell_time; // 建仓时间
	private String gdsy_buy_itemcode;// 建仓品种
	private String gdsy_buy_itemname;// 商品名称
	private double gdsy_buy_brokerage;// 建仓手续费
	private double gdsy_buy_price;// 建仓单价
	private int gdsy_buy_amount;// 建仓手数
	private double gdsy_buy_all_price;// 建仓总价
	private int gdsy_buy_xz_time;// 建仓所选时间
	private double gdsy_buy_ratio;// 建仓盈亏率
	private String is_use;// 是否可用

	private String confirm; // 确认

	public int getGdsy_buy_xz_time() {
		return gdsy_buy_xz_time;
	}

	public void setGdsy_buy_xz_time(int gdsy_buy_xz_time) {
		this.gdsy_buy_xz_time = gdsy_buy_xz_time;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getDeptment_id() {
		return deptment_id;
	}

	public void setDeptment_id(String deptment_id) {
		this.deptment_id = deptment_id;
	}

	public String getBroker_id() {
		return broker_id;
	}

	public void setBroker_id(String broker_id) {
		this.broker_id = broker_id;
	}

	public String getGdsy_order_id() {
		return gdsy_order_id;
	}

	public void setGdsy_order_id(String gdsy_order_id) {
		this.gdsy_order_id = gdsy_order_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGdsy_buy_type() {
		return gdsy_buy_type;
	}

	public void setGdsy_buy_type(String gdsy_buy_type) {
		this.gdsy_buy_type = gdsy_buy_type;
	}

	public Date getGdsy_buy_time() {
		return gdsy_buy_time;
	}

	public void setGdsy_buy_time(Date gdsy_buy_time) {
		this.gdsy_buy_time = gdsy_buy_time;
	}

	public double getGdsy_buy_point() {
		return gdsy_buy_point;
	}

	public void setGdsy_buy_point(double gdsy_buy_point) {
		this.gdsy_buy_point = gdsy_buy_point;
	}

	public String getGdsy_buy_itemcode() {
		return gdsy_buy_itemcode;
	}

	public void setGdsy_buy_itemcode(String gdsy_buy_itemcode) {
		this.gdsy_buy_itemcode = gdsy_buy_itemcode;
	}

	public double getGdsy_buy_price() {
		return gdsy_buy_price;
	}

	public void setGdsy_buy_price(double gdsy_buy_price) {
		this.gdsy_buy_price = gdsy_buy_price;
	}

	public double getGdsy_buy_all_price() {
		return gdsy_buy_all_price;
	}

	public void setGdsy_buy_all_price(double gdsy_buy_all_price) {
		this.gdsy_buy_all_price = gdsy_buy_all_price;
	}

	public int getGdsy_buy_amount() {
		return gdsy_buy_amount;
	}

	public void setGdsy_buy_amount(int gdsy_buy_amount) {
		this.gdsy_buy_amount = gdsy_buy_amount;
	}

	public double getGdsy_buy_brokerage() {
		return gdsy_buy_brokerage;
	}

	public void setGdsy_buy_brokerage(double gdsy_buy_brokerage) {
		this.gdsy_buy_brokerage = gdsy_buy_brokerage;
	}

	public String getIs_use() {
		return is_use;
	}

	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}

	public double getGdsy_buy_ratio() {
		return gdsy_buy_ratio;
	}

	public void setGdsy_buy_ratio(double gdsy_buy_ratio) {
		this.gdsy_buy_ratio = gdsy_buy_ratio;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getGdsy_buy_itemname() {
		return gdsy_buy_itemname;
	}

	public void setGdsy_buy_itemname(String gdsy_buy_itemname) {
		this.gdsy_buy_itemname = gdsy_buy_itemname;
	}

	public double getGdsy_sell_point() {
		return gdsy_sell_point;
	}

	public void setGdsy_sell_point(double gdsy_sell_point) {
		this.gdsy_sell_point = gdsy_sell_point;
	}

	public Date getGdsy_sell_time() {
		return gdsy_sell_time;
	}

	public void setGdsy_sell_time(Date gdsy_sell_time) {
		this.gdsy_sell_time = gdsy_sell_time;
	}
}

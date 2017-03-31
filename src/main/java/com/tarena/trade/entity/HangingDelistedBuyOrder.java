package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class HangingDelistedBuyOrder implements Serializable {

	private static final long serialVersionUID = -8015785702874814687L;

	private String gzp_order_id; // 主键id

	private String agent_id; // 代理商id
	private String deptment_id; // 部门ID
	private String broker_id; // 经纪人id
	private String user_id; // 用户id

	private String gzp_buy_type; // 方向
	private double gzp_buy_point; // 建仓点位
	private Date gzp_buy_time; // 建仓时间
	private double gzp_sell_point; // 建仓点位
	private Date gzp_sell_time; // 建仓时间
	private String gzp_buy_itemcode; // 建仓品种
	private String gzp_buy_itemname; // 商品名称
	private double gzp_buy_brokerage; // 总手续费
	private double gzp_buy_price; // 建仓单价
	private int gzp_buy_amount; // 建仓手数
	private double gzp_buy_all_price; // 建仓总价
	private double gzp_profit; // 波动盈亏
	private String is_use; // 是否可用

	private String confirm; // 确认否
	private double point;
	
	private String zc_order_id;
	private String is_zc;

	public double getPoint() {
    return point;
  }

  public void setPoint(double point) {
    this.point = point;
  }

  public String getGzp_order_id() {
		return gzp_order_id;
	}

	public void setGzp_order_id(String gzp_order_id) {
		this.gzp_order_id = gzp_order_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGzp_buy_type() {
		return gzp_buy_type;
	}

	public void setGzp_buy_type(String gzp_buy_type) {
		this.gzp_buy_type = gzp_buy_type;
	}

	public Date getGzp_buy_time() {
		return gzp_buy_time;
	}

	public void setGzp_buy_time(Date gzp_buy_time) {
		this.gzp_buy_time = gzp_buy_time;
	}

	public double getGzp_buy_point() {
		return gzp_buy_point;
	}

	public void setGzp_buy_point(double gzp_buy_point) {
		this.gzp_buy_point = gzp_buy_point;
	}

	public String getGzp_buy_itemcode() {
		return gzp_buy_itemcode;
	}

	public void setGzp_buy_itemcode(String gzp_buy_itemcode) {
		this.gzp_buy_itemcode = gzp_buy_itemcode;
	}

	public double getGzp_buy_price() {
		return gzp_buy_price;
	}

	public void setGzp_buy_price(double gzp_buy_price) {
		this.gzp_buy_price = gzp_buy_price;
	}

	public double getGzp_buy_all_price() {
		return gzp_buy_all_price;
	}

	public void setGzp_buy_all_price(double gzp_buy_all_price) {
		this.gzp_buy_all_price = gzp_buy_all_price;
	}

	public int getGzp_buy_amount() {
		return gzp_buy_amount;
	}

	public void setGzp_buy_amount(int gzp_buy_amount) {
		this.gzp_buy_amount = gzp_buy_amount;
	}

	public double getGzp_buy_brokerage() {
		return gzp_buy_brokerage;
	}

	public void setGzp_buy_brokerage(double gzp_buy_brokerage) {
		this.gzp_buy_brokerage = gzp_buy_brokerage;
	}

	public String getIs_use() {
		return is_use;
	}

	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getBroker_id() {
		return broker_id;
	}

	public void setBroker_id(String broker_id) {
		this.broker_id = broker_id;
	}

	public String getDeptment_id() {
		return deptment_id;
	}

	public void setDeptment_id(String deptment_id) {
		this.deptment_id = deptment_id;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public double getGzp_sell_point() {
		return gzp_sell_point;
	}

	public void setGzp_sell_point(double gzp_sell_point) {
		this.gzp_sell_point = gzp_sell_point;
	}

	public Date getGzp_sell_time() {
		return gzp_sell_time;
	}

	public void setGzp_sell_time(Date gzp_sell_time) {
		this.gzp_sell_time = gzp_sell_time;
	}

	public double getGzp_profit() {
		return gzp_profit;
	}

	public void setGzp_profit(double gzp_profit) {
		this.gzp_profit = gzp_profit;
	}

	public String getGzp_buy_itemname() {
		return gzp_buy_itemname;
	}

	public void setGzp_buy_itemname(String gzp_buy_itemname) {
		this.gzp_buy_itemname = gzp_buy_itemname;
	}

	public String getZc_order_id() {
		return zc_order_id;
	}

	public void setZc_order_id(String zc_order_id) {
		this.zc_order_id = zc_order_id;
	}

	public String getIs_zc() {
		return is_zc;
	}

	public void setIs_zc(String is_zc) {
		this.is_zc = is_zc;
	}
	
	
}

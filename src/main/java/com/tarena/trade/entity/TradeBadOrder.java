package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class TradeBadOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 794774731262212562L;
	
	private String id;
	private String user_id;
	private String profitloss_id;
	private String order_id;
	private double bad_money;
	private String bad_type;
	private String bad_desc;
	private Date bad_time;
	private String bad_status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getProfitloss_id() {
		return profitloss_id;
	}

	public void setProfitloss_id(String profitloss_id) {
		this.profitloss_id = profitloss_id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public double getBad_money() {
		return bad_money;
	}

	public void setBad_money(double bad_money) {
		this.bad_money = bad_money;
	}

	public String getBad_type() {
		return bad_type;
	}

	public void setBad_type(String bad_type) {
		this.bad_type = bad_type;
	}

	public String getBad_desc() {
		return bad_desc;
	}

	public void setBad_desc(String bad_desc) {
		this.bad_desc = bad_desc;
	}

	public Date getBad_time() {
		return bad_time;
	}

	public void setBad_time(Date bad_time) {
		this.bad_time = bad_time;
	}

	public String getBad_status() {
		return bad_status;
	}

	public void setBad_status(String bad_status) {
		this.bad_status = bad_status;
	}

}

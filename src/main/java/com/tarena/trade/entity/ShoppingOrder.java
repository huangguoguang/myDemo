package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class ShoppingOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String shopping_order_id;
	private String order_id;
	private String item_name;
	private String item_name_alias;
	private double amount;
	private double pre_payment;
	private double balance_payment;    //订单预付款
	private String receiver_name;      //订单尾款
	private String receiver_mobile; 
	private String receiver_addr;
	private String user_id;
	private String p_id;
	private String ce_id;
	private String ch_id;
	private String settle_id;
	private String settle_name;
	private String agent_id;
	private String agent_name;
	private String broker_id;
	private String broker_name;
	private String dept_id;
	private String dept_name;
	private String is_use;
	private Date create_date;
	
	
	private String status;    //1用户已付款代发货/2用户已付款已发货/3订单完成
	private double buy_price; // 建仓单价
	private int buy_amount;   // 建仓手数
	private String tracking_number;
	private String express;
	private String buy_unit;
	private String user_name;     
	private String user_mobile;
	private String buy_type;
	private Date buy_time;
	private String item_code;
	private String buy_itemtype;
	private int buy_brokerage = 0;      //手续费
	private double buy_all_price;
	private String item_id;
	private double buy_point;
	private String buy_number;
	private double sell_point;
	
	
	
	public String getShopping_order_id() {
		return shopping_order_id;
	}
	public void setShopping_order_id(String shopping_order_id) {
		this.shopping_order_id = shopping_order_id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getItem_name_alias() {
		return item_name_alias;
	}
	public void setItem_name_alias(String item_name_alias) {
		this.item_name_alias = item_name_alias;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getPre_payment() {
		return pre_payment;
	}
	public void setPre_payment(double pre_payment) {
		this.pre_payment = pre_payment;
	}
	public double getBalance_payment() {
		return balance_payment;
	}
	public void setBalance_payment(double balance_payment) {
		this.balance_payment = balance_payment;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public String getReceiver_addr() {
		return receiver_addr;
	}
	public void setReceiver_addr(String receiver_addr) {
		this.receiver_addr = receiver_addr;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getP_id() {
		return p_id;
	}
	public void setP_id(String p_id) {
		this.p_id = p_id;
	}
	public String getCe_id() {
		return ce_id;
	}
	public void setCe_id(String ce_id) {
		this.ce_id = ce_id;
	}
	public String getCh_id() {
		return ch_id;
	}
	public void setCh_id(String ch_id) {
		this.ch_id = ch_id;
	}
	public String getSettle_id() {
		return settle_id;
	}
	public void setSettle_id(String settle_id) {
		this.settle_id = settle_id;
	}
	public String getSettle_name() {
		return settle_name;
	}
	public void setSettle_name(String settle_name) {
		this.settle_name = settle_name;
	}
	public String getAgent_id() {
		return agent_id;
	}
	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}
	public String getAgent_name() {
		return agent_name;
	}
	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}
	public String getBroker_id() {
		return broker_id;
	}
	public void setBroker_id(String broker_id) {
		this.broker_id = broker_id;
	}
	public String getBroker_name() {
		return broker_name;
	}
	public void setBroker_name(String broker_name) {
		this.broker_name = broker_name;
	}
	public String getDept_id() {
		return dept_id;
	}
	public void setDept_id(String dept_id) {
		this.dept_id = dept_id;
	}
	public String getDept_name() {
		return dept_name;
	}
	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getBuy_price() {
		return buy_price;
	}
	public void setBuy_price(double buy_price) {
		this.buy_price = buy_price;
	}
	public int getBuy_amount() {
		return buy_amount;
	}
	public void setBuy_amount(int buy_amount) {
		this.buy_amount = buy_amount;
	}
	public String getTracking_number() {
		return tracking_number;
	}
	public void setTracking_number(String tracking_number) {
		this.tracking_number = tracking_number;
	}
	public String getExpress() {
		return express;
	}
	public void setExpress(String express) {
		this.express = express;
	}
	public String getBuy_unit() {
		return buy_unit;
	}
	public void setBuy_unit(String buy_unit) {
		this.buy_unit = buy_unit;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_mobile() {
		return user_mobile;
	}
	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}
	public String getBuy_type() {
		return buy_type;
	}
	public void setBuy_type(String buy_type) {
		this.buy_type = buy_type;
	}
	public Date getBuy_time() {
		return buy_time;
	}
	public void setBuy_time(Date buy_time) {
		this.buy_time = buy_time;
	}
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	public String getBuy_itemtype() {
		return buy_itemtype;
	}
	public void setBuy_itemtype(String buy_itemtype) {
		this.buy_itemtype = buy_itemtype;
	}
	public int getBuy_brokerage() {
		return buy_brokerage;
	}
	public void setBuy_brokerage(int buy_brokerage) {
		this.buy_brokerage = buy_brokerage;
	}
	public double getBuy_all_price() {
		return buy_all_price;
	}
	public void setBuy_all_price(double buy_all_price) {
		this.buy_all_price = buy_all_price;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public double getBuy_point() {
		return buy_point;
	}
	public void setBuy_point(double buy_point) {
		this.buy_point = buy_point;
	}
	public String getBuy_number() {
		return buy_number;
	}
	public void setBuy_number(String buy_number) {
		this.buy_number = buy_number;
	}
	public double getSell_point() {
		return sell_point;
	}
	public void setSell_point(double sell_point) {
		this.sell_point = sell_point;
	}
	
	
}

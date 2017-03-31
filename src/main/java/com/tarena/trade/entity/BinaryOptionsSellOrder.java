package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class BinaryOptionsSellOrder implements Serializable {

	private static final long serialVersionUID = -6580256617307827160L;

	private String p_id; // 平台id
	private String ch_id; // 渠道id
	private String ce_id; // 交易中心id
	private String settle_id; // 所属结算会员单位
	private String agent_id; // 所属代理商
	private String dept_id; // 所属部门
	private String broker_id; // 所属经纪人id
	private String user_id; // 用户id

	private String gdsy_order_id; // 订单主键id

	private String gdsy_buy_type; // 方向
	private double gdsy_buy_point; // 建仓点位
	private Date gdsy_buy_time; // 建仓时间
	private String gdsy_buy_itemcode; // 建仓品种
	private double gdsy_buy_price; // 建仓单价
	private double gdsy_buy_all_price; // 建仓总价
	private int gdsy_buy_amount; // 建仓手数
	private double gdsy_buy_brokerage; // 建仓手续费
	private double gdsy_buy_ratio; // 建仓盈亏率
	private Date gdsy_sell_time; // 平仓时间
	private double gdsy_sell_point; // 平仓点位
	private String gdsy_sell_type; // 平仓类型
	private double gdsy_sell_profit_loss; // 平仓盈亏
	private double gdsy_settlement_value; // 结算金额
	private Date create_date;
	private String is_use; // 是否可用
	private String rule;
	

	public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public String getP_id() {
		return p_id;
	}

	public void setP_id(String p_id) {
		this.p_id = p_id;
	}

	public String getCh_id() {
		return ch_id;
	}

	public void setCh_id(String ch_id) {
		this.ch_id = ch_id;
	}

	public String getCe_id() {
		return ce_id;
	}

	public void setCe_id(String ce_id) {
		this.ce_id = ce_id;
	}

	public String getSettle_id() {
		return settle_id;
	}

	public void setSettle_id(String settle_id) {
		this.settle_id = settle_id;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getDept_id() {
		return dept_id;
	}

	public void setDept_id(String dept_id) {
		this.dept_id = dept_id;
	}

	public String getBroker_id() {
		return broker_id;
	}

	public void setBroker_id(String broker_id) {
		this.broker_id = broker_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGdsy_order_id() {
		return gdsy_order_id;
	}

	public void setGdsy_order_id(String gdsy_order_id) {
		this.gdsy_order_id = gdsy_order_id;
	}

	public String getGdsy_buy_type() {
		return gdsy_buy_type;
	}

	public void setGdsy_buy_type(String gdsy_buy_type) {
		this.gdsy_buy_type = gdsy_buy_type;
	}

	public double getGdsy_buy_point() {
		return gdsy_buy_point;
	}

	public void setGdsy_buy_point(double gdsy_buy_point) {
		this.gdsy_buy_point = gdsy_buy_point;
	}

	public Date getGdsy_buy_time() {
		return gdsy_buy_time;
	}

	public void setGdsy_buy_time(Date gdsy_buy_time) {
		this.gdsy_buy_time = gdsy_buy_time;
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

	public double getGdsy_buy_ratio() {
		return gdsy_buy_ratio;
	}

	public void setGdsy_buy_ratio(double gdsy_buy_ratio) {
		this.gdsy_buy_ratio = gdsy_buy_ratio;
	}

	public Date getGdsy_sell_time() {
		return gdsy_sell_time;
	}

	public void setGdsy_sell_time(Date gdsy_sell_time) {
		this.gdsy_sell_time = gdsy_sell_time;
	}

	public double getGdsy_sell_point() {
		return gdsy_sell_point;
	}

	public void setGdsy_sell_point(double gdsy_sell_point) {
		this.gdsy_sell_point = gdsy_sell_point;
	}

	public String getGdsy_sell_type() {
		return gdsy_sell_type;
	}

	public void setGdsy_sell_type(String gdsy_sell_type) {
		this.gdsy_sell_type = gdsy_sell_type;
	}

	public double getGdsy_sell_profit_loss() {
		return gdsy_sell_profit_loss;
	}

	public void setGdsy_sell_profit_loss(double gdsy_sell_profit_loss) {
		this.gdsy_sell_profit_loss = gdsy_sell_profit_loss;
	}

	public double getGdsy_settlement_value() {
		return gdsy_settlement_value;
	}

	public void setGdsy_settlement_value(double gdsy_settlement_value) {
		this.gdsy_settlement_value = gdsy_settlement_value;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public String getIs_use() {
		return is_use;
	}

	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}

	@Override
	public String toString() {
		return "BinaryOptionsSellOrder [p_id=" + p_id + ", ch_id=" + ch_id + ", ce_id=" + ce_id + ", settle_id=" + settle_id
				+ ", agent_id=" + agent_id + ", dept_id=" + dept_id + ", broker_id=" + broker_id + ", user_id=" + user_id
				+ ", gdsy_order_id=" + gdsy_order_id + ", gdsy_buy_type=" + gdsy_buy_type + ", gdsy_buy_point=" + gdsy_buy_point
				+ ", gdsy_buy_time=" + gdsy_buy_time + ", gdsy_buy_itemcode=" + gdsy_buy_itemcode + ", gdsy_buy_price="
				+ gdsy_buy_price + ", gdsy_buy_all_price=" + gdsy_buy_all_price + ", gdsy_buy_amount=" + gdsy_buy_amount
				+ ", gdsy_buy_brokerage=" + gdsy_buy_brokerage + ", gdsy_buy_ratio=" + gdsy_buy_ratio + ", gdsy_sell_time="
				+ gdsy_sell_time + ", gdsy_sell_point=" + gdsy_sell_point + ", gdsy_sell_type=" + gdsy_sell_type
				+ ", gdsy_sell_profit_loss=" + gdsy_sell_profit_loss + ", gdsy_settlement_value=" + gdsy_settlement_value
				+ ", create_date=" + create_date + ", is_use=" + is_use + "]";
	}

}

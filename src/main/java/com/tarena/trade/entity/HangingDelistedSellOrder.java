package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class HangingDelistedSellOrder implements Serializable {

	private static final long serialVersionUID = -4698523620564473100L;

	private String p_id; // 平台id
	private String ch_id; // 渠道id
	private String ce_id; // 交易中心id
	private String settle_id; // 所属结算会员单位
	private String agent_id; // 所属代理商
	private String dept_id; // 所属部门
	private String broker_id; // 所属经纪人
	private String user_id; // 用户ID

	private String gzp_order_id; // 平仓订单

	private String gzp_buy_type; // 建仓类型
	private double gzp_buy_point; // 建仓点位
	private Date gzp_buy_time; // 建仓时间
	private String gzp_buy_itemcode; // 建仓品种
	private double gzp_buy_price; // 建仓单价
	private double gzp_buy_all_price;// 建仓总价
	private int gzp_buy_amount;// 建仓手数
	private double gzp_buy_brokerage;// 建仓手续费
	private double gzp_sell_point; // 平仓点位
	private Date gzp_sell_time; // 平仓时间
	private String gzp_sell_type; // 平仓类型
	private double gzp_sell_zy_point; // 止盈点位
	private double gzp_sell_zs_point; // 止损点位
	private double gzp_sell_profit_loss; // 平仓盈亏
	private double gzp_settlement_value; // 结算金额
	private double gzp_profit; // 波动盈亏
	private String is_use; // 是否可用

	private Date create_date; // 创建时间
	private String rule; // 01:挂摘牌 02:点位期权
	
	private String zc_order_id;
	private String is_zc;

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

	public String getGzp_order_id() {
		return gzp_order_id;
	}

	public void setGzp_order_id(String gzp_order_id) {
		this.gzp_order_id = gzp_order_id;
	}

	public String getGzp_buy_type() {
		return gzp_buy_type;
	}

	public void setGzp_buy_type(String gzp_buy_type) {
		this.gzp_buy_type = gzp_buy_type;
	}

	public double getGzp_buy_point() {
		return gzp_buy_point;
	}

	public void setGzp_buy_point(double gzp_buy_point) {
		this.gzp_buy_point = gzp_buy_point;
	}

	public Date getGzp_buy_time() {
		return gzp_buy_time;
	}

	public void setGzp_buy_time(Date gzp_buy_time) {
		this.gzp_buy_time = gzp_buy_time;
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

	public String getGzp_sell_type() {
		return gzp_sell_type;
	}

	public void setGzp_sell_type(String gzp_sell_type) {
		this.gzp_sell_type = gzp_sell_type;
	}

	public double getGzp_sell_zy_point() {
		return gzp_sell_zy_point;
	}

	public void setGzp_sell_zy_point(double gzp_sell_zy_point) {
		this.gzp_sell_zy_point = gzp_sell_zy_point;
	}

	public double getGzp_sell_zs_point() {
		return gzp_sell_zs_point;
	}

	public void setGzp_sell_zs_point(double gzp_sell_zs_point) {
		this.gzp_sell_zs_point = gzp_sell_zs_point;
	}

	public double getGzp_sell_profit_loss() {
		return gzp_sell_profit_loss;
	}

	public void setGzp_sell_profit_loss(double gzp_sell_profit_loss) {
		this.gzp_sell_profit_loss = gzp_sell_profit_loss;
	}

	public double getGzp_settlement_value() {
		return gzp_settlement_value;
	}

	public void setGzp_settlement_value(double gzp_settlement_value) {
		this.gzp_settlement_value = gzp_settlement_value;
	}

	public double getGzp_profit() {
		return gzp_profit;
	}

	public void setGzp_profit(double gzp_profit) {
		this.gzp_profit = gzp_profit;
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

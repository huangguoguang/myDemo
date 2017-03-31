package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class TradeBuyOrderMongodb implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4448291214705688044L;

//	private String id;
	private String order_id; // 订单号
	
	private String p_id; // 平台id
	private String ch_id; // 渠道id
	private String ce_id; // 交易中心id
	private String settle_id; // 结算
	private String settle_name;
	private String agent_id; // 代理商ID
	private String agent_name;
	private String dept_id; // 部门ID
	private String dept_name;
	private String broker_id; // 经纪人ID
	private String broker_name; 
	private String user_id; // 用户
	private String user_name;
	private String user_mobile; // 手机号

	private String buy_type; // 方向
	private double buy_point; // 建仓点位
	private String buy_unit; // 单位
	private Date buy_time; // 建仓时间
	private Date sell_time; // 平仓时间
	private String buy_itemtype; // 建仓数型
	private String buy_itemcode;// 建仓品种
	private String buy_itemname; // 商品名称
	private double buy_brokerage = 0; // 建仓手续费
	private double buy_price; // 建仓单价
	private int buy_amount; // 建仓手数
	private double buy_all_price; // 建仓总价
	private String is_use; // 是否可用

	private String buy_number; // 数型
	// 固定收益独有
	private int gdsy_buy_xz_time; // 建仓所选时间
	private double gdsy_buy_ratio; // 建仓盈亏率
	// 挂摘牌独有
	private double gzp_profit; // 波动盈亏
	private double gzp_sell_zs_point; // 止损点位
	private double gzp_sell_zy_point; // 止盈点位

	private Date create_date;

	private String item_id;

	// 0无效 1 有效 2建仓中 3待平仓 4平仓中 5坏单
	private String confirm; // 确认

	// 挂摘牌/二元期权
	private String trading_rule;
	
	private double point;
	
	private String buy_itemname_alias;
	
	private String step;
	
	private String zc_order_id;
	private String is_zc;
	

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

	

	public double getPoint() {
    return point;
  }

  public String getBuy_itemname_alias() {
		return buy_itemname_alias;
	}

	public void setBuy_itemname_alias(String buy_itemname_alias) {
		this.buy_itemname_alias = buy_itemname_alias;
	}

public void setPoint(double point) {
    this.point = point;
  }

  public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
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

	public double getBuy_point() {
		return buy_point;
	}

	public void setBuy_point(double buy_point) {
		this.buy_point = buy_point;
	}

	public String getBuy_unit() {
		return buy_unit;
	}

	public void setBuy_unit(String buy_unit) {
		this.buy_unit = buy_unit;
	}

	public Date getBuy_time() {
		return buy_time;
	}

	public void setBuy_time(Date buy_time) {
		this.buy_time = buy_time;
	}

	public Date getSell_time() {
		return sell_time;
	}

	public void setSell_time(Date sell_time) {
		this.sell_time = sell_time;
	}

	public String getBuy_itemtype() {
		return buy_itemtype;
	}

	public void setBuy_itemtype(String buy_itemtype) {
		this.buy_itemtype = buy_itemtype;
	}

	public String getBuy_itemcode() {
		return buy_itemcode;
	}

	public void setBuy_itemcode(String buy_itemcode) {
		this.buy_itemcode = buy_itemcode;
	}

	public String getBuy_itemname() {
		return buy_itemname;
	}

	public void setBuy_itemname(String buy_itemname) {
		this.buy_itemname = buy_itemname;
	}

	public double getBuy_brokerage() {
		return buy_brokerage;
	}

	public void setBuy_brokerage(double buy_brokerage) {
		this.buy_brokerage = buy_brokerage;
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

	public double getBuy_all_price() {
		return buy_all_price;
	}

	public void setBuy_all_price(double buy_all_price) {
		this.buy_all_price = buy_all_price;
	}

	public String getIs_use() {
		return is_use;
	}

	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}

	public String getBuy_number() {
		return buy_number;
	}

	public void setBuy_number(String buy_number) {
		this.buy_number = buy_number;
	}

	public int getGdsy_buy_xz_time() {
		return gdsy_buy_xz_time;
	}

	public void setGdsy_buy_xz_time(int gdsy_buy_xz_time) {
		this.gdsy_buy_xz_time = gdsy_buy_xz_time;
	}

	public double getGdsy_buy_ratio() {
		return gdsy_buy_ratio;
	}

	public void setGdsy_buy_ratio(double gdsy_buy_ratio) {
		this.gdsy_buy_ratio = gdsy_buy_ratio;
	}

	public double getGzp_profit() {
		return gzp_profit;
	}

	public void setGzp_profit(double gzp_profit) {
		this.gzp_profit = gzp_profit;
	}

	public double getGzp_sell_zs_point() {
		return gzp_sell_zs_point;
	}

	public void setGzp_sell_zs_point(double gzp_sell_zs_point) {
		this.gzp_sell_zs_point = gzp_sell_zs_point;
	}

	public double getGzp_sell_zy_point() {
		return gzp_sell_zy_point;
	}

	public void setGzp_sell_zy_point(double gzp_sell_zy_point) {
		this.gzp_sell_zy_point = gzp_sell_zy_point;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getTrading_rule() {
		return trading_rule;
	}

	public void setTrading_rule(String trading_rule) {
		this.trading_rule = trading_rule;
	}

	public String getSettle_name() {
		return settle_name;
	}

	public void setSettle_name(String settle_name) {
		this.settle_name = settle_name;
	}

	public String getAgent_name() {
		return agent_name;
	}

	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}

	public String getDept_name() {
		return dept_name;
	}

	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}

	public String getBroker_name() {
		return broker_name;
	}

	public void setBroker_name(String broker_name) {
		this.broker_name = broker_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	
	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
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

	@Override
	public String toString() {
		return "TradeBuyOrderMongodb [" + ", order_id=" + order_id + ", p_id=" + p_id + ", ch_id=" + ch_id
				+ ", ce_id=" + ce_id + ", settle_id=" + settle_id + ", settle_name=" + settle_name + ", agent_id=" + agent_id
				+ ", agent_name=" + agent_name + ", dept_id=" + dept_id + ", dept_name=" + dept_name + ", broker_id="
				+ broker_id + ", broker_name=" + broker_name + ", user_id=" + user_id + ", user_name=" + user_name
				+ ", user_mobile=" + user_mobile + ", buy_type=" + buy_type + ", buy_point=" + buy_point + ", buy_unit="
				+ buy_unit + ", buy_time=" + buy_time + ", sell_time=" + sell_time + ", buy_itemtype=" + buy_itemtype
				+ ", buy_itemcode=" + buy_itemcode + ", buy_itemname=" + buy_itemname + ", buy_brokerage=" + buy_brokerage
				+ ", buy_price=" + buy_price + ", buy_amount=" + buy_amount + ", buy_all_price=" + buy_all_price + ", is_use="
				+ is_use + ", buy_number=" + buy_number + ", gdsy_buy_xz_time=" + gdsy_buy_xz_time + ", gdsy_buy_ratio="
				+ gdsy_buy_ratio + ", gzp_profit=" + gzp_profit + ", gzp_sell_zs_point=" + gzp_sell_zs_point
				+ ", gzp_sell_zy_point=" + gzp_sell_zy_point + ", create_date=" + create_date + ", item_id=" + item_id
				+ ", confirm=" + confirm + ", trading_rule=" + trading_rule + "]";
	}

}

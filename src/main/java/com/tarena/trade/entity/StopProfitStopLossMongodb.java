package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * className:StopProfitStopLossMongodb author:zhangyi createDate:2016年8月13日
 * 下午2:40:00 vsersion:1.0 department:安创乐科技 description:
 */
public class StopProfitStopLossMongodb implements Serializable {
  
  private static final long serialVersionUID = -4708476399657772529L;
  
//  private String id;
  
  private String user_id;
  
  private String order_id;
  
  private Double gzp_sell_zy_point;
  
  private double gzp_sell_zs_point;
  
  private String buy_type;
  
  private String buy_itemcode;
  
  private String buy_itemtype;
  
  private double buy_point;
  
  private double sell_point;
  
  private String confirm;
  
  private Date sell_date;
  
  private String sell_type;
  
  private String trading_rule;
  
  private double point;

  public String getTrading_rule() {
    return trading_rule;
  }

  public void setTrading_rule(String trading_rule) {
    this.trading_rule = trading_rule;
  }

  public double point() {
    return point;
  }

  public void setPoint(double point) {
    this.point = point;
  }

  public String getSell_type() {
    return sell_type;
  }

  public void setSell_type(String sell_type) {
    this.sell_type = sell_type;
  }

//  public String getId() {
//    return id;
//  }
//
//  public void setId(String id) {
//    this.id = id;
//  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getOrder_id() {
    return order_id;
  }

  public void setOrder_id(String order_id) {
    this.order_id = order_id;
  }

  public Double getGzp_sell_zy_point() {
    return gzp_sell_zy_point;
  }

  public void setGzp_sell_zy_point(Double gzp_sell_zy_point) {
    this.gzp_sell_zy_point = gzp_sell_zy_point;
  }

  public double getGzp_sell_zs_point() {
    return gzp_sell_zs_point;
  }

  public void setGzp_sell_zs_point(double gzp_sell_zs_point) {
    this.gzp_sell_zs_point = gzp_sell_zs_point;
  }

  public String getBuy_type() {
    return buy_type;
  }

  public void setBuy_type(String buy_type) {
    this.buy_type = buy_type;
  }

  public String getBuy_itemcode() {
    return buy_itemcode;
  }

  public void setBuy_itemcode(String buy_itemcode) {
    this.buy_itemcode = buy_itemcode;
  }

  public double getBuy_point() {
    return buy_point;
  }

  public void setBuy_point(double buy_point) {
    this.buy_point = buy_point;
  }

  public double getSell_point() {
    return sell_point;
  }

  public void setSell_point(double sell_point) {
    this.sell_point = sell_point;
  }

  public String getConfirm() {
    return confirm;
  }

  public void setConfirm(String confirm) {
    this.confirm = confirm;
  }

  public Date getSell_date() {
    return sell_date;
  }

  public void setSell_date(Date sell_date) {
    this.sell_date = sell_date;
  }

	public String getBuy_itemtype() {
		return buy_itemtype;
	}

	public void setBuy_itemtype(String buy_itemtype) {
		this.buy_itemtype = buy_itemtype;
	}

}

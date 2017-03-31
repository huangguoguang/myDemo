package com.tarena.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class HangingDelistedItem implements Serializable {
  
  private static final long serialVersionUID = 8650266606357959190L;
  
  private String gzp_id;//主键id
  private String gzp_name;//商品名称
  private String gzp_code;//商品标号
  private double gzp_number;//商品数型
  private int gzp_order;//商品排序
  private String gzp_unit;//商品单位
  private String gzp_type;//商品类型
  private String is_use;//是否可用
  private Date create_date;//创建时间
  private double gzp_money;//商品价格
  private double gzp_poundage;//商品手续费
  private double gzp_profit;//波动盈亏
  private int gzp_lot;//商品手数
  private String gzp__buy_time;//商品可交易时间
  private String gzp__buy_date;//商品可交易日期
  
  public String getGzp_id() {
    return gzp_id;
  }
  public void setGzp_id(String gzp_id) {
    this.gzp_id = gzp_id;
  }
  public String getGzp_name() {
    return gzp_name;
  }
  public void setGzp_name(String gzp_name) {
    this.gzp_name = gzp_name;
  }
  public String getGzp_code() {
    return gzp_code;
  }
  public void setGzp_code(String gzp_code) {
    this.gzp_code = gzp_code;
  }
  public double getGzp_number() {
    return gzp_number;
  }
  public void setGzp_number(double gzp_number) {
    this.gzp_number = gzp_number;
  }
  public int getGzp_order() {
    return gzp_order;
  }
  public void setGzp_order(int gzp_order) {
    this.gzp_order = gzp_order;
  }
  public String getGzp_unit() {
    return gzp_unit;
  }
  public void setGzp_unit(String gzp_unit) {
    this.gzp_unit = gzp_unit;
  }
  public String getGzp_type() {
    return gzp_type;
  }
  public void setGzp_type(String gzp_type) {
    this.gzp_type = gzp_type;
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
  public double getGzp_money() {
    return gzp_money;
  }
  public void setGzp_money(double gzp_money) {
    this.gzp_money = gzp_money;
  }
  public double getGzp_poundage() {
    return gzp_poundage;
  }
  public void setGzp_poundage(double gzp_poundage) {
    this.gzp_poundage = gzp_poundage;
  }
  public double getGzp_profit() {
    return gzp_profit;
  }
  public void setGzp_profit(double gzp_profit) {
    this.gzp_profit = gzp_profit;
  }
  public int getGzp_lot() {
    return gzp_lot;
  }
  public void setGzp_lot(int gzp_lot) {
    this.gzp_lot = gzp_lot;
  }
  public String getGzp__buy_time() {
    return gzp__buy_time;
  }
  public void setGzp__buy_time(String gzp__buy_time) {
    this.gzp__buy_time = gzp__buy_time;
  }
  public String getGzp__buy_date() {
    return gzp__buy_date;
  }
  public void setGzp__buy_date(String gzp__buy_date) {
    this.gzp__buy_date = gzp__buy_date;
  }

}

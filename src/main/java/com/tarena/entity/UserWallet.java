package com.tarena.entity;

import java.io.Serializable;
import java.util.Date;

public class UserWallet implements Serializable {

  private static final long serialVersionUID = -2204602248798253355L;

  private String wallet_id;// 主键id

  private String user_id;// 用户id

  private double user_money;// 用户金额

  private String type; // 类型 加/减

  private double order_money;// 订单金额

  private String is_use;// 是否可用

  private Date create_date;// 创建时间

  private Date update_date;// 修改时间

  public double getUser_money() {
    return user_money;
  }

  public void setUser_money(double user_money) {
    this.user_money = user_money;
  }

  public double getOrder_money() {
    return order_money;
  }

  public void setOrder_money(double order_money) {
    this.order_money = order_money;
  }

  public String getWallet_id() {
    return wallet_id;
  }

  public void setWallet_id(String wallet_id) {
    this.wallet_id = wallet_id;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
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

  public Date getUpdate_date() {
    return update_date;
  }

  public void setUpdate_date(Date update_date) {
    this.update_date = update_date;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}

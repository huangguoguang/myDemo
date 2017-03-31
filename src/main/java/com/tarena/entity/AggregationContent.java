package com.tarena.entity;

import java.io.Serializable;

public class AggregationContent implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private long holder_lot;
  
  private String trading_rule;
  
  private String buy_itemcode;
  
  private String buy_type;
  
  private String buy_number;

  public long getHolder_lot() {
    return holder_lot;
  }

  public void setHolder_lot(long holder_lot) {
    this.holder_lot = holder_lot;
  }

  public String getTrading_rule() {
    return trading_rule;
  }

  public void setTrading_rule(String trading_rule) {
    this.trading_rule = trading_rule;
  }

  public String getBuy_itemcode() {
    return buy_itemcode;
  }

  public void setBuy_itemcode(String buy_itemcode) {
    this.buy_itemcode = buy_itemcode;
  }

  public String getBuy_type() {
    return buy_type;
  }

  public void setBuy_type(String buy_type) {
    this.buy_type = buy_type;
  }

  public String getBuy_number() {
    return buy_number;
  }

  public void setBuy_number(String buy_number) {
    this.buy_number = buy_number;
  }
  
  

}

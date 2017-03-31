package com.tarena.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 前台用户实体类
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 290288634830340116L;
  
  //sessionKey      登录后保存用户信息
  public final static String sessionKey = "userInfo"; 
  //sessionKeyLogin 登录之前保存部分用户信息
  public final static String sessionKeyLogin = "userInfoLogin"; 
  
  //用户基本信息
  private String user_id;        		//用户id
  private String broker_code; 			//所属经纪人编号	
  private String broker_id; 				//经纪人的id	
  private String broker_name; 			//经纪人的名字
  private String broker_incode; 		//所属经纪人邀请码	
  private String user_name;					//用户名
  private String user_last_update;	//最后修改时间
  private Date 	 user_login_date;		//最后登录时间
  private Date 	 create_date;				//创建时间
  private String is_use;						//是否可用
  private String user_ip;						//登录ip
  private String agent_id;					//位属代理商id
  private String dept_id;					  //位属部门id
  private String dept_name;					//位属部门名称
  private String agent_code;				//位属代理商编号
  private String agent_name;				//代理商名字
  private String settle_id;					//位属结算id
  private String settle_name;				//结算名字
  private String head_url;					//用户头像
  private String ce_id;							//交易中心id
  private String ch_id;							//渠道id
  private String ce_name;						//交易中心名称
  private String ch_name;						//渠道名称
  private String p_id;							//平台id
  private String own_broker_id;// 自身经纪人Id
  
  //钱包信息
  private String wallet_id;					//钱包id
  private String user_money;				//余额
  private String update_date;				//余额
  
  
  
  //登录信息
  private String user_mobile;				//用户密文手机号
  private String user_demobile;			//用户明文手机号
  private String user_wxid;					//微信openid
  private String user_pay_id;				//微信支付id
  private String user_password;			//密码
  private String user_login_id;			//用户登录id
  
  
  	
	public String getOwn_broker_id() {
		return own_broker_id;
	}

	public void setOwn_broker_id(String own_broker_id) {
		this.own_broker_id = own_broker_id;
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


	public String getBroker_incode() {
		return broker_incode;
	}


	public void setBroker_incode(String broker_incode) {
		this.broker_incode = broker_incode;
	}


	public String getUser_id() {
		return user_id;
	}


	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	public String getBroker_code() {
		return broker_code;
	}


	public void setBroker_code(String broker_code) {
		this.broker_code = broker_code;
	}


	public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getUser_last_update() {
		return user_last_update;
	}


	public void setUser_last_update(String user_last_update) {
		this.user_last_update = user_last_update;
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


	public String getUser_ip() {
		return user_ip;
	}


	public void setUser_ip(String user_ip) {
		this.user_ip = user_ip;
	}


	public String getAgent_id() {
		return agent_id;
	}


	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}


	public String getAgent_code() {
		return agent_code;
	}


	public void setAgent_code(String agent_code) {
		this.agent_code = agent_code;
	}


	public String getSettle_id() {
		return settle_id;
	}


	public void setSettle_id(String settle_id) {
		this.settle_id = settle_id;
	}


	public String getHead_url() {
		return head_url;
	}


	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}


	public String getWallet_id() {
		return wallet_id;
	}


	public void setWallet_id(String wallet_id) {
		this.wallet_id = wallet_id;
	}


	public String getUser_money() {
		return user_money;
	}


	public void setUser_money(String user_money) {
		this.user_money = user_money;
	}


	public String getUpdate_date() {
		return update_date;
	}


	public void setUpdate_date(String update_date) {
		this.update_date = update_date;
	}


	public String getUser_mobile() {
		return user_mobile;
	}


	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}


	public String getUser_wxid() {
		return user_wxid;
	}


	public void setUser_wxid(String user_wxid) {
		this.user_wxid = user_wxid;
	}


	public String getUser_pay_id() {
		return user_pay_id;
	}


	public void setUser_pay_id(String user_pay_id) {
		this.user_pay_id = user_pay_id;
	}


	public String getUser_password() {
		return user_password;
	}


	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}


	public String getUser_login_id() {
		return user_login_id;
	}


	public void setUser_login_id(String user_login_id) {
		this.user_login_id = user_login_id;
	}


	public static String getSessionkey() {
		return sessionKey;
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


	public String getAgent_name() {
		return agent_name;
	}


	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}


	public String getSettle_name() {
		return settle_name;
	}


	public void setSettle_name(String settle_name) {
		this.settle_name = settle_name;
	}


	public String getCe_name() {
		return ce_name;
	}


	public void setCe_name(String ce_name) {
		this.ce_name = ce_name;
	}


	public String getCh_name() {
		return ch_name;
	}


	public void setCh_name(String ch_name) {
		this.ch_name = ch_name;
	}


	public String getDept_id() {
		return dept_id;
	}


	public void setDept_id(String dept_id) {
		this.dept_id = dept_id;
	}


	public String getP_id() {
		return p_id;
	}


	public void setP_id(String p_id) {
		this.p_id = p_id;
	}


	public Date getUser_login_date() {
		return user_login_date;
	}


	public void setUser_login_date(Date user_login_date) {
		this.user_login_date = user_login_date;
	}


	public String getDept_name() {
		return dept_name;
	}


	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}


	public String getUser_demobile() {
		return user_demobile;
	}


	public void setUser_demobile(String user_demobile) {
		this.user_demobile = user_demobile;
	}
  
  
  @Override
	public String toString() {
		return "UserInfo [user_id=" + user_id + ", broker_code=" + broker_code
				+ ", broker_id=" + broker_id + ", broker_name=" + broker_name
				+ ", broker_incode=" + broker_incode + ", user_name=" + user_name
				+ ", user_last_update=" + user_last_update + ", user_login_date="
				+ user_login_date + ", create_date=" + create_date + ", is_use="
				+ is_use + ", user_ip=" + user_ip + ", agent_id=" + agent_id
				+ ", dept_id=" + dept_id + ", dept_name=" + dept_name + ", agent_code="
				+ agent_code + ", agent_name=" + agent_name + ", settle_id="
				+ settle_id + ", settle_name=" + settle_name + ", head_url=" + head_url
				+ ", ce_id=" + ce_id + ", ch_id=" + ch_id + ", ce_name=" + ce_name
				+ ", ch_name=" + ch_name + ", p_id=" + p_id + ", wallet_id="
				+ wallet_id + ", user_money=" + user_money + ", update_date="
				+ update_date + ", user_mobile=" + user_mobile + ", user_demobile="
				+ user_demobile + ", user_wxid=" + user_wxid + ", user_pay_id="
				+ user_pay_id + ", user_password=" + user_password + ", user_login_id="
				+ user_login_id + "]";
	}
  

}

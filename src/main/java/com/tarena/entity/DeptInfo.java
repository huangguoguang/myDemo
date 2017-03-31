package com.tarena.entity;

import java.io.Serializable;

/** 
 * @author: chenwei
 * @version：1.0 
 * @创建时间：2016年8月10日 下午1:42:35 
 * @说明：部门(结算、代理商...)
 *
 */
public class DeptInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3238471073472497519L;
	
	private String dept_id; 					//部门id
	private String dept_code; 				//部门编号
	private String dept_name;					//部门名字
	private String dept_mobile;				//部门联系方式
	private String create_date;				//创建时间
	private String is_use; 						//是否可用
	private String dept_money; 				//部门余额
	private String dept_ratio; 				//部门占比
	private String dept_type; 				//部门类型
	private String dept_database; 		//部门数据库
	private String dept_url;    			//部门的url
	private String dept_parent_id; 		//上级id
	private String Dept_parent_name; 	//上级名字(需要后台在写入部门的时候往mongodb中增量插入)
	
	public String getDept_id() {
		return dept_id;
	}
	public void setDept_id(String dept_id) {
		this.dept_id = dept_id;
	}
	public String getDept_code() {
		return dept_code;
	}
	public void setDept_code(String dept_code) {
		this.dept_code = dept_code;
	}
	public String getDept_name() {
		return dept_name;
	}
	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	public String getDept_mobile() {
		return dept_mobile;
	}
	public void setDept_mobile(String dept_mobile) {
		this.dept_mobile = dept_mobile;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public String getIs_use() {
		return is_use;
	}
	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}
	public String getDept_money() {
		return dept_money;
	}
	public void setDept_money(String dept_money) {
		this.dept_money = dept_money;
	}
	public String getDept_ratio() {
		return dept_ratio;
	}
	public void setDept_ratio(String dept_ratio) {
		this.dept_ratio = dept_ratio;
	}
	public String getDept_type() {
		return dept_type;
	}
	public void setDept_type(String dept_type) {
		this.dept_type = dept_type;
	}
	public String getDept_database() {
		return dept_database;
	}
	public void setDept_database(String dept_database) {
		this.dept_database = dept_database;
	}
	public String getDept_url() {
		return dept_url;
	}
	public void setDept_url(String dept_url) {
		this.dept_url = dept_url;
	}
	public String getDept_parent_id() {
		return dept_parent_id;
	}
	public void setDept_parent_id(String dept_parent_id) {
		this.dept_parent_id = dept_parent_id;
	}
	public String getDept_parent_name() {
		return Dept_parent_name;
	}
	public void setDept_parent_name(String dept_parent_name) {
		Dept_parent_name = dept_parent_name;
	}

}

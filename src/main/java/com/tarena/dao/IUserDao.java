package com.tarena.dao;

import java.util.List;
import java.util.Map;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.tarena.entity.UserInfo;

public interface IUserDao {

	//登录验证
	public UserInfo loginCheck(Map<String,Object> map);
	
	//查询代理商id和结算id
	public Map<String,Object> getUserDeptAndSettle(Map<String,Object> map);
	
	//判断手机号是否重复
	public List<Map<String,Object>> checkMobileIsUse(String userMobile);
	
	//判断微信号是否重复
	public List<Map<String,Object>> checkWxOpenIdIsUse(String wxOpenId);
	
	//判断经纪人邀请码是否可用
	public List<Map<String,Object>> validateBrokerCode(Map<String,Object> map);
	
	//插入用户信息
	public void insertUserInfo(UserInfo userinfo);
	
	//插入用户登录信息
	public void insertUserLoginInfo(UserInfo userinfo);
	
	//插入用户钱包信息
	public void insertUserWallet(UserInfo userinfo);
	
	//修改用户密码
	public void modifyUserPassword(Map<String,Object> map);
	
	//取得当前用户收据库的余额
	public Map<String,Object> queryUserMoney(String user_id);
	
  //查看代理商appid
	public Map<String,Object> queryAgentInfoByAgentCode(String ageng_code);
	
	//更改经纪人状态
	public void updateBrokerStatus(Map<String,Object> map);
	
	//查询未读信息的条数
	public int queryUserMsg(String user_id);
	
	
	//更新用户的openID
	public void updateUserOpenID(Map<String,Object> map);
	
	//查询固定收益商品信息
	public List<Map<String,String>> findGdsyItem(String agent_code);
	
	//查询挂摘牌商品信息
	public List<Map<String,String>> findGzpItem(String agent_code);
	
	//查询商城历史订单
	public PageList<Map<String,Object>> queryShoppingOrder(Map<String,Object> map,PageBounds pageBounds);
	
	
	//查询商品图片
    public List<Map<String,Object>> getItemImage();
}

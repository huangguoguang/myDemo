package com.tarena.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.UncategorizedSQLException;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.entity.UserInfo;
import com.tarena.exception.UserLoginException;
import com.tarena.exception.UserModifyPasswordException;
import com.tarena.exception.UserRegisterException;
import com.tarena.util.Pagination;


/** 
 * @author: chenwei
 * @version：1.0 
 * @创建时间：2016年8月5日 下午3:28:21 
 * 用户接口
 */ 
@SuppressWarnings("rawtypes")
public interface IUserService {


	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * @说明：用户登录
	 *
	 */ 
	public Object loginCheck(Map<String,Object> map,UserInfo userInfo) throws NullPointerException,UserLoginException;
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * @说明：用户注册
	 *
	 */ 
	public String userRegister(Map<String,Object> map,SessionProvider session,HttpServletRequest request) throws UncategorizedSQLException, UserRegisterException;
	
	/** 
	 * @throws UserRegisterException 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * @说明：用户忘记密码
	 *
	 */ 
	public void modifyPassword(Map<String,Object> map,SessionProvider session) throws UserModifyPasswordException;
	
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * 设置用户钱包信息
	 */
	public boolean setRedisUserWallet(UserInfo userInfo)throws NullPointerException;
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * 查询代理商的appid
	 */
	public Map queryAgentInfoByAgentCode(String agent_code);
	
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * 交易主页的用户信息
	 */
	public Map<String,Object> getUserMain(SessionProvider session)  throws UserLoginException;
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * 查询用户的未读信息
	 */

	public Pagination<Map> queryUserMsg(String center_type,SessionProvider session,String column,int page,int pageSize,String collection)  throws ParseException;
	
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * 信息阅读
	 */
	public void readMsg(SessionProvider session,String center_id) throws ParseException;
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * 信息阅读
	 */
	public void readAllMsg(SessionProvider session) throws ParseException;
	
	/** 
	 * @创建时间：2016年8月8日 下午4:23:17 
	 * 收支明细
	 */
	//public Pagination<UserProfitLoss> queryDetail(String user_id, String firstDate,String lastDate, String column,int pageNo, int pageSize, Class<UserProfitLoss> cs, String collection) throws ParseException;
	
	
	/** 
	 * @创建时间：2016年8月23日 下午9:01:21 
	 * 查询信息详情
	 */ 
	public Map getMsg(SessionProvider session,String center_id);
	
	
	
	/** 
	 * @创建时间：2016年8月23日 下午9:01:21 
	 * 代理商商品大类
	 */ 
	public Map<String,Object> findItemInfo(String agent_code);
	
	/**
	 * 查询商城历史订单
	 * @param map
	 * @param pageBounds
	 * @return
	 */
	public PageList<Map<String, Object>> queryShoppingOrder(Map<String, Object> map, PageBounds pageBounds);

	
	public List<Map<String, Object>> findItemImage();
}

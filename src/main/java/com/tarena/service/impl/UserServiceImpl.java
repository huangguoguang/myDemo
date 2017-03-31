package com.tarena.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.tarena.common.component.session.SessionProvider;
import com.tarena.dao.IUserDao;
import com.tarena.entity.UserInfo;
import com.tarena.exception.UserLoginException;
import com.tarena.exception.UserModifyPasswordException;
import com.tarena.exception.UserRegisterException;
import com.tarena.lock.DistributeLock;
import com.tarena.mongo.service.MongodbBaseDao;
import com.tarena.service.IUserService;
import com.tarena.util.MD5Utils;
import com.tarena.util.MySecurity;
import com.tarena.util.NumberUtil;
import com.tarena.util.Pagination;
import com.tarena.util.ValidateUtil;

@Service
public class UserServiceImpl implements IUserService {

  @Autowired
  private IUserDao iUserDao;
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  
  @Autowired
  private MongodbBaseDao<UserInfo> userMongodbDao;
  
  @SuppressWarnings("rawtypes")
  @Autowired
  private MongodbBaseDao<Map> mongoDao;
  
  @Autowired
  private DistributeLock distributeLock;
  
  private static Logger logger = Logger.getLogger(UserServiceImpl.class);

  @Override
  public Object loginCheck(Map<String, Object> map, UserInfo userInfo)
      throws NullPointerException, UserLoginException {
    String login_name = map.get("username").toString();
    Map<String,Object> queryMap = new HashMap<String, Object>();
    List<UserInfo> user = new ArrayList<UserInfo>();
    //判断代理商是否启用
    if(ValidateUtil.isChinaPhoneLegal(login_name)){
      //如果是手机号
      queryMap.put("user_mobile", MySecurity.encryptAES(login_name,"ANCHOL2016"));
      queryMap.put("agent_code", map.get("agent_code").toString()); 
      queryMap.put("user_password", MD5Utils.MD5(map.get("password").toString()));
      queryMap.put("is_use", "1");
      user = userMongodbDao.find(queryMap, UserInfo.class,"t_wp_front_user_login");
    }
    if(user.size() == 0) {
      throw new UserLoginException("账号或密码错误");
    }
    //更新用户最后登录时间
    if(user.size() == 1){
      UserInfo queryUser = user.get(0);
 
      //解密用户手机号
      queryUser.setUser_demobile(MySecurity.decrypt(queryUser.getUser_mobile(),"ANCHOL2016"));
      Update update  = new Update();
      update.set("user_login_date", new Date());
      mongoTemplate.upsert(new Query(Criteria.where("user_id").is(queryUser.getUser_id())), update, "t_wp_front_user_login");
      //如果redis中没有用户余额,初始化余额
      setRedisUserWallet(queryUser);
    }
    
    if(user.size()>1){
      throw new UserLoginException("用户账号异常");
    }
    List<Object> reList = new ArrayList<Object>();
    reList.add(user.get(0));
    return reList;
  }

  @Override
  public String userRegister(Map<String, Object> map, SessionProvider session,
      HttpServletRequest request) throws UncategorizedSQLException,
      UserRegisterException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void modifyPassword(Map<String, Object> map, SessionProvider session)
      throws UserModifyPasswordException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean setRedisUserWallet(UserInfo userInfo)
      throws NullPointerException {
    String lockKeyActivityLuckydraw = distributeLock.getLock("wallet", userInfo.getUser_id());
    if (lockKeyActivityLuckydraw == null) {
      return false;
    }
    try{
    if (stringRedisTemplate.opsForValue().get("wallet_"+userInfo.getUser_id())== null){
        logger.info("用户:"+userInfo.getUser_name()+"_"+userInfo.getUser_id()+"登录时在redis当中没有取到余额,将为他重新写入余额");
        //取得当前用户数据库中的余额
        Map<String,Object> map =  iUserDao.queryUserMoney(userInfo.getUser_id());  
        logger.info("正在写入中...");
        logger.info("当前时间为:"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        stringRedisTemplate.opsForValue().set("wallet_"+userInfo.getUser_id(),map.get("user_money").toString());
        logger.info("写入成功");
    }
      return true;
    }finally{
      distributeLock.releaseLock("wallet", userInfo.getUser_id(), lockKeyActivityLuckydraw);
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Map queryAgentInfoByAgentCode(String agent_code) {
    Map map = mongoTemplate.findOne(new Query(Criteria.where("dept_code").is(agent_code)), Map.class,"t_wp_back_dept_info");
    return map;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Map<String, Object> getUserMain(SessionProvider session)
      throws UserLoginException {
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    if(userInfo == null) {
      logger.info("登录成功用户为空");
      throw new UserLoginException("登录异常");
    }
    //获取用户头像
    String headimgurl;
    if(session.getAttribute("headimgurl") !=null) {
      headimgurl = session.getAttribute("headimgurl").toString();
    }else{
      throw new UserLoginException("未取到用户头像");
    }
    //获取用户登录id
    String user_login_id = userInfo.getUser_login_id();
    //获取用户名称
    String user_name = userInfo.getUser_name();
    //得到用户的金额
    if(stringRedisTemplate.opsForValue().get("wallet_"+userInfo.getUser_id())== null){
      throw new UserLoginException("获取用户余额失败");
    }
    String user_money = stringRedisTemplate.opsForValue().get("wallet_"+userInfo.getUser_id());
    String mb = userInfo.getUser_demobile();
    String user_mobile = mb.substring(0, 3) + "****" + mb.substring(7, mb.length());
    
    //查询未读重要信息
    Query query = new Query();
    query.addCriteria(Criteria.where("user_id").is(userInfo.getUser_id()));
    query.fields().include("center_id");
    
    //查询已读信息
    List<Map> read_msg_list = mongoTemplate.find(query, Map.class, "t_wp_center_info_user");

    String[] center_id = new String[read_msg_list.size()];
    for(int i = 0;i<read_msg_list.size();i++){
      center_id[i] = read_msg_list.get(i).get("center_id").toString();
    }
    List<Map> important_msg_list = mongoDao.findImportantMsg("center_id",center_id,Map.class,"centerInfo");
    
    Map<String,Object> resultMap = new HashMap<String, Object>();
    //得到支付URL 
    String entrances = stringRedisTemplate.opsForValue().get("entrances");
    String banner = stringRedisTemplate.opsForValue().get("banner_key");
    if(entrances == null){
      resultMap.put("pay_url","http://mp.weixin.qq.com/s?__biz=MzI2MDUxODQ4OQ==&mid=100000006&idx=1&sn=fca9c5eff106ca2c9ada4d944c412dc5&chksm=6a6926305d1eaf26a36d6830359250315beb50ef3368aa4c46bbc203a66bd0d83f7bdfa2ad41#rd");
    }else{
      JSONArray array = (JSONArray) JSONArray.parse(entrances);
      for(int i = 0;i< array.size();i++){
        JSONObject obj = array.getJSONObject(i);
        String agent_code = obj.getString("agent_code");
        if(agent_code.contains("#"+userInfo.getAgent_code()+"#")){
          resultMap.put("pay_url", obj.getString("url"));
        }
        if(resultMap.get("pay_url")==null){
          resultMap.put("pay_url","http://mp.weixin.qq.com/s?__biz=MzI2MDUxODQ4OQ==&mid=100000006&idx=1&sn=fca9c5eff106ca2c9ada4d944c412dc5&chksm=6a6926305d1eaf26a36d6830359250315beb50ef3368aa4c46bbc203a66bd0d83f7bdfa2ad41#rd");
        }
      }
    }
    resultMap.put("banner",banner);
    resultMap.put("headimgurl",headimgurl);                        
    resultMap.put("user_money",NumberUtil.fotmatNum(user_money));  
    resultMap.put("user_name" ,user_name);                        
    resultMap.put("user_mobile",user_mobile);                       
    resultMap.put("user_login_id",user_login_id);                     
    resultMap.put("important_msg_list", important_msg_list);                
    resultMap.put("agent_code" , userInfo.getAgent_code());           

    return resultMap;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Pagination<Map> queryUserMsg(String center_type,
      SessionProvider session, String column, int page, int pageSize,
      String collection) throws ParseException {
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    //获得分页查询的信息列表 
    Pagination<Map> pageMsgList = mongoDao.queryUserMsg(center_type,column,page,pageSize,Map.class,collection); 
    //查询已读的信息列表 
    Map<String,Object> queryMap = new HashMap<String, Object>();
    queryMap.put("user_id", userInfo.getUser_id());
    //用户中间信息表
    List<Map> msgUserList = mongoDao.find(queryMap, Map.class, "t_wp_center_info_user");
    //所有信息列表 
    List<Map> msgList = pageMsgList.getRows();
    
    if(msgUserList.size() == 0){
      for(Map msgMap:msgList){
        msgMap.put("is_read", false);
      }
    }else{
      for(int i = 0 ;i<msgList.size();i++){
        int m = 0;
        for(int j=0;j<msgUserList.size();j++){
          if(msgUserList.get(j).get("center_id").toString().equals(msgList.get(i).get("center_id").toString())){
            m++;
          }
        }
        if(m > 0){
          msgList.get(i).put("is_read", true);
        }else {
          msgList.get(i).put("is_read", false);
        }
      }
    }
    pageMsgList.setRows(msgList);
    return pageMsgList;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void readMsg(SessionProvider session, String center_id)
      throws ParseException {
    UserInfo userInfo = (UserInfo) session.getAttribute(UserInfo.sessionKey);
    Map<String,Object> queryMap = new HashMap<String, Object>();
    queryMap.put("center_id", center_id);
    queryMap.put("user_id", userInfo.getUser_id());
    List<Map> msgUserList = mongoDao.find(queryMap, Map.class,"t_wp_center_info_user");//存放已读信息中间表
    if(msgUserList.size() == 0){
      //表示这条信息没有读取过
      //mongo中添加一条
      mongoDao.save(queryMap, "t_wp_center_info_user");
    }
    //同时把信息的阅读次数加1
    queryMap.remove("user_id");
    List<Map> msgCenterList = mongoDao.find(queryMap, Map.class, "centerInfo");//存放所有信息表，包括已阅读，未阅读
    Object center_number = msgCenterList.get(0).get("center_number");
    if(center_number == null || center_number.equals("")){
      //如果阅读次数为空，将其置为1,否则加1
      center_number = 1;
    }else {
      center_number  = Integer.parseInt(center_number.toString()) + 1;
    }
    Update update = new Update();
    update.set("center_number", String.valueOf(center_number));
    mongoDao.updateFirst(queryMap, update, "centerInfo");
  }

  @Override
  public void readAllMsg(SessionProvider session) throws ParseException {
    
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Map getMsg(SessionProvider session, String center_id) {
    Query query = new Query();
    query.addCriteria(Criteria.where("center_id").is(center_id));
    query.fields().exclude("_id");
    return mongoTemplate.findOne(query, Map.class, "centerInfo");
  }

  @Override
  public Map<String, Object> findItemInfo(String agent_code) {
    List<Map<String, String>> gdsy_list = iUserDao.findGdsyItem(agent_code);
    List<Map<String, String>> gzp_list = iUserDao.findGzpItem(agent_code);
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("gdsy", gdsy_list);
    map.put("gzp", gzp_list);
    return map;
  }

  @Override
  public PageList<Map<String, Object>> queryShoppingOrder(
      Map<String, Object> map, PageBounds pageBounds) {
    return iUserDao.queryShoppingOrder(map, pageBounds);
  }

  @Override
  public List<Map<String, Object>> findItemImage() {
    return iUserDao.getItemImage();
  }
  
 
}

package com.tarena.redis.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.github.miemiedev.mybatis.paginator.domain.Order.Direction;
import com.tarena.baseTest.SpringTestCase;
import com.tarena.entity.User;
import com.tarena.entity.UserInfo;
import com.tarena.mongo.service.MongodbBaseDao;
import com.tarena.util.MD5Utils;
import com.tarena.util.MySecurity;
import com.tarena.util.Pagination;

public class TestMongo extends SpringTestCase{

  @Autowired
  private MongodbBaseDao<UserInfo> userdao;
  @Autowired
  private MongodbBaseDao<User> userDao;
  
  @Autowired
  private MongodbBaseDao<Map> mongoDao;
  
  @Autowired
  private MongoTemplate mongotmplate;
  @Test
  @Ignore
  public void test(){
    Map<String,Object> queryMap = new HashMap<String, Object>();
    String login_name = "18612344499";
    String agent_code = "zyhj";
    String password = "111111";
    queryMap.put("user_mobile", MySecurity.encryptAES(login_name,"ANCHOL2016"));
    queryMap.put("agent_code",agent_code); 
    queryMap.put("user_password", MD5Utils.MD5(password));
    queryMap.put("is_use", "1");
    List<UserInfo> user = userdao.find(queryMap, UserInfo.class,"t_wp_front_user_login");
    System.out.println(user.size());
  }
  
//  @Test
//  @Ignore
//  public void testfind(){
//    User user = new User();
//    user.setUsername("aaa");
//    user.setPassword("111111");
//    userDao.save(user, "chenwei");
//  }
//  
//  @Test
//  @Ignore
//  public void get(){
//    List<User> user = userDao.find(new Query(Criteria.where("username").is("aaa")), User.class, "chenwei");
//    System.out.println(user.get(0).getPassword());
//  }
  
  @Test
  @Ignore
  public void getUserInfo(){
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("user_mobile", "Njg3NDdCREVFREU5OTBBMDA2NjQzNUNFNDAzNEIwMzk=");
    map.put("user_password", "96E79218965EB72C92A549DD5A330112");
    map.put("is_use", "1");
    map.put("agent_code", "zyhj");
    List<UserInfo> user = userdao.find(map, UserInfo.class, "t_wp_front_user_login");
    System.out.println(user.get(0).getAgent_code());
  }
  
  @Test
  @Ignore
  public void testTotalOrder(){
    long i = mongotmplate.count(new Query(Criteria.where("user_id").is("94BF70B561904A5994F8022856718BE2").and("confirm").ne("0")), "TradeBuyOrderMongodb");
    System.out.println(i);
  }
  
  @Test
  @Ignore
  public void testmsg(){
    Query query = new Query();
    query.addCriteria(new Criteria().where("user_id").is("94BF70B561904A5994F8022856718BE2"));
    
    List<Map> read_msg_list = mongotmplate.find(query, Map.class, "t_wp_center_info_user");
    String [] centet_id = new  String [read_msg_list.size()];
    System.out.println(read_msg_list.size());
    for(int i = 0;i<read_msg_list.size();i++){
      centet_id[i] = read_msg_list.get(i).get("center_id").toString();
      System.out.println(centet_id[i]);
    }
    List<Map> impartant_msg_list = mongoDao.findImportantMsg("center_id", centet_id, Map.class, "centerInfo");
    System.out.println(impartant_msg_list.size());
  }
  
  @Test
  @Ignore
  public <T> void testGetMsg(){
    Query query = new Query();
    query.addCriteria(new Criteria().where("center_type").is("1").and("is_use").is("1"));
    query.fields().exclude("_id");
    query.fields().include("center_title").include("center_text");
    long count = mongotmplate.count(query, "centerInfo");
    Pagination<T> page = new Pagination<T>(1, 3, count);
    query.skip(page.getFirstResult());
    query.limit(3);
    
    List<T> rows = (List<T>) mongoDao.find(query, Map.class, "centerInfo");
    for(int i = 0 ;i<rows.size();i++){
      System.out.println(rows.get(i).toString());
    }
    page.setRows(rows);
    System.out.println(page.getRows().size());
  }
  
  @Test
  public void testAmount(){
    Query query = new Query();
    query.addCriteria(new Criteria().where("user_id").is("94BF70B561904A5994F8022856718BE2")
        .and("trading_rule").is("01").and("confirm").ne("0").and("is_zc").ne("1").and("buy_type").is("buy")
        .and("buy_itemcode").is("ag01"));
    List<Map> map =mongotmplate.find(query, Map.class, "TradeBuyOrderMongodb");
    
    int sum = 0;
    for(Map m : map){
      int amount = (int) m.get("buy_amount");
      sum = sum + amount;
      System.out.println(m.get("buy_amount"));
    }
    System.out.println(sum);
  }
}

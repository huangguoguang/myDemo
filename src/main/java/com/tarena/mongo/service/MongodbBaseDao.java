package com.tarena.mongo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.tarena.util.Pagination;

public class MongodbBaseDao<T> {
  protected MongoTemplate mongoTemplate;

  public void setMongoTemplate(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }
  
  /**
   * 保存一个对象对Mongodb
   * @param map
   * @param collectionName
   * @return
   */
  public Map<String,Object> save(Map<String, Object> map, String collectionName){
    mongoTemplate.save(map,collectionName);
    return map;
  }
  
  /**
   * 保存一个对象到Mongodb
   * @param object
   * @param collection
   */
  public void save(Object object, String collection){
    mongoTemplate.save(object, collection);
  }
  
  /**
   * 按条件查询，并且删除
   * @param query
   * @param cs
   * @return
   */
  public T findAndRemove(Query query ,Class<T> cs){
    return this.mongoTemplate.findAndRemove(query, cs);
  }
  
  /**
   * 根据条件删除集合中的一条记录
   * @param object
   * @param collection
   */
  public void remove(Map<String , Object> map, String collection){
    Criteria channel = new Criteria();
    channel.andOperator(MongoUtil.covertMap(map));
    this.mongoTemplate.remove(new Query(channel), collection);
  }
  
  /**
   * 根据Id删除某个集合中的一条记录
   * @param value
   * @param collection
   */
  public void remove(String value, String collection){
    this.mongoTemplate.remove(new Query(Criteria.where("user_id").is(value)), collection);
  }
  
  /**
   * 通过条件查询更新数据
   * @param query
   * @param update
   * @param entityClass
   */
  public void updateFirst(Query query ,Update update,Class<?> entityClass){
    mongoTemplate.updateFirst(query, update, entityClass);
  }
  
  public void updateFirst(Map<String,Object> map,Update update,String collection){
    Criteria channel = new Criteria();
    channel.andOperator(MongoUtil.covertMap(map));
    Query query = new Query(channel);
    mongoTemplate.updateFirst(query, update, collection);
  }
  
  /**
   * 根据条件指更新
   * @param value
   * @param update
   * @param collection
   */
  public void updateMulti(String value,Update update,String collection){
    mongoTemplate.updateMulti(new Query(Criteria.where("_id").is(value)), update, collection);
  }
  
  /**
   * 条件查询不分页
   * @param query
   */
  public List<T> find(Query query, Class<T> cs, String collection) {
    return mongoTemplate.find(query, cs, collection);
  }
  
  /**
   * 条件查询
   * @return
   */
  public List<T> find(Map<String, Object> map, Class<T> cs, String collection) {
    Criteria channleIdCri = new Criteria();
    channleIdCri.andOperator(MongoUtil.covertMap(map));
    Query query = new Query();
    query.addCriteria(channleIdCri);
    return mongoTemplate.find(query, cs, collection);
  }

  /**
   * 查询未读的重要信息
   * @param string
   * @param key
   * @param class1
   * @param collection
   * @return
   */
  public List<T> findImportantMsg(String key, String[] center_id,
      Class<T> cs, String collection) {
    
    Query query = new Query();
    Criteria channleIdCri = new Criteria();
    channleIdCri.andOperator(MongoUtil.convertMapNe(key,center_id));
    query.fields().exclude("_id");
    query.addCriteria(channleIdCri);
    return mongoTemplate.find(query, cs, collection);
  }

  /**
   * 查询所有的消息
   * @param pageNo
   * @param pageSize
   * @param center_type "1"表示前台信息 "2"表示交易规则
   * @throws ParseException 
   */
  public Pagination<T> queryUserMsg(String center_type, String column,
      int pageNo, int pageSize, Class<T> class1, String collection) {
    Query query = new Query(Criteria.where("center_type").is(center_type).and("is_use").is("1"));
    query.fields().include("center_title").exclude("_id").include("center_id").include("is_important").include("create_date").include("center_number");
    long totalCount = mongoTemplate.count(query, collection);
    Pagination<T> page = new Pagination<T>(pageNo, pageSize, totalCount);
    query.skip(page.getFirstResult());
    query.limit(pageSize);
    Direction direction = Direction.DESC;
    query.with(new Sort(direction, column));
    List<T> rows = find(query, class1, collection);
    page.setRows(rows);
    return page;
  }

  /**
   * 通过 ID查询更新数据
   * @param value
   * @param update
   * @param collection
   */
  public void updateFirstById(String value, Update update,
      String collection) {
    mongoTemplate.updateFirst(new Query(Criteria.where("order_id").is(value)), update, collection);
  }

  /**
   * 根据Id删除一条记录
   * @param order_id
   * @param mongodbTableTradebuyorder
   */
  public void removeById(String value, String collection) {
    mongoTemplate.remove(new Query(Criteria.where("order_id").is(value)), collection);
  }

  /**
   * 查询已平仓订单
   * @param user_id
   * @param firstDate
   * @param lastDate
   * @param column
   * @param page
   * @param pageSize
   * @param collection
   * @return
   * @throws ParseException 
   */
  public Pagination<T> queryOrder(String user_id, String firstDate,
      String lastDate, String column, int page, int pageSize,Class<T> cs, String collection) throws ParseException {
    //时间补全
    String start_time = firstDate +" " +"00:00:00";
    String end_time = lastDate + " "+"23:59:59";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date beginDate = sdf.parse(start_time);
    Date endDate = sdf.parse(end_time);
    
    Criteria channleIdCri = new Criteria();
    channleIdCri.andOperator(
        Criteria.where(column).gte(beginDate),
        Criteria.where(column).lte(endDate),
        Criteria.where("user_id").is(user_id),
        Criteria.where("is_use").is("1"));
    Query query = new Query();
    query.fields().exclude("_id").exclude("user_mobile");
    query.addCriteria(channleIdCri);
    
    long totalCount = this.mongoTemplate.count(query, collection);
    Pagination<T> pages = new Pagination<T>(page, pageSize, totalCount);
    query.skip(pages.getFirstResult());//skip相当于从哪条记录开始
    query.limit(pageSize);//从skip开始，取多少条记录
    Direction direction = Direction.DESC;
    query.with(new Sort(direction, column));//sell_time降序排列
    
    List<T> rows = this.find(query, cs, collection);
    pages.setRows(rows);
    return pages;
  }
}

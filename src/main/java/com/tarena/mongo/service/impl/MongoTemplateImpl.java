package com.tarena.mongo.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.tarena.entity.UserInfo;
import com.tarena.mongo.service.AbstractMongoTemplete;

@Service
public class MongoTemplateImpl implements AbstractMongoTemplete{
  @Autowired
  private MongoTemplate mongoTemplate;
  private String collection = "m_user";

  @Override
  public UserInfo findOne(int id) {
    Query query = new Query(Criteria.where("id").is(id));
    return mongoTemplate.findOne(query, UserInfo.class, collection);
  }

  @Override
  public List<UserInfo> findAll() {
    return mongoTemplate.findAll(UserInfo.class, collection);
  }

  @Override
  public void deleteOne(Query query,String collectionName) {
    mongoTemplate.remove(query, collectionName);
  }

  @Override
  public void updateFirst(Query query, Update update,String collectionName) {
    mongoTemplate.updateFirst(query, update, collectionName);
  }

  @Override
  public Map<String, Object> save(Map<String, Object> map, String collectionName) {
    mongoTemplate.save(map, collectionName);
    return map;
  }

  @Override
  public void save(Object obj, String collectionName) {
    mongoTemplate.save(obj, collectionName);
  }
  
}

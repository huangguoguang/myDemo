package com.tarena.mongo.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.tarena.entity.UserInfo;

public interface AbstractMongoTemplete {
  public Map<String,Object> save(Map<String,Object> map,String collectionName);
  public void save(Object obj,String collectionName);
  public UserInfo findOne(int id);
  public List<UserInfo> findAll();
  public void updateFirst(Query query,Update update,String collectionName);
  public void deleteOne(Query query,String collectionName);
}

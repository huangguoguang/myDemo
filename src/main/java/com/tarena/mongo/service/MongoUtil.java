package com.tarena.mongo.service;

import java.util.Map;

import org.springframework.data.mongodb.core.query.Criteria;

public class MongoUtil {

  public static Criteria[] covertMap(Map<String, Object> map) {
    Criteria[] cris = new Criteria[map.size()];
    int i = 0;
    for(String key :map.keySet()){
      cris[i] = Criteria.where(key).is(map.get(key));
      i++;
    }
    return cris;
  }

  public static Criteria[] convertMapNe(String key, String[] str) {
    Criteria[] ce = new Criteria[str.length + 3];
    if(str.length != 0){
      int i =0;
      for(int j = 0;j<str.length;j++){
        ce[j] = Criteria.where(key).ne(str[j]);
        i++;
      }
      ce[i] = Criteria.where("is_important").is("1");
      ce[i+1] = Criteria.where("is_use").is("1");
      ce[i+2] = Criteria.where("center_type").is("1");
    }else{
      ce[0] = Criteria.where("is_important").is("1");
      ce[1] = Criteria.where("center_type").is("1");
      ce[2] = Criteria.where("is_use").is("1");
    }
    
    return ce;
  }
}

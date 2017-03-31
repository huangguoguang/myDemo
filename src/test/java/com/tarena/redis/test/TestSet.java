package com.tarena.redis.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TestSet {
  public static void main(String[] args) {
    Set<String> s = new HashSet<String>();
    s.add("abc");
    s.add("bcd");
    s.add("abc");
    s.add("aaa");
    System.out.println(s.size());
    Iterator<String> it= s.iterator();
    while(it.hasNext()){
      System.out.println(it.next());
    }
    
    
    System.out.println(s.iterator().next());
  }
}

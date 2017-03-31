package com.tarena.util.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCollections {
  public static void main(String[] args) {
    
    List<String> list = new ArrayList<String>();
    list.add("aaa");
    list.add("bbb");
    System.out.println(list);
    list = Collections.singletonList("ccc");
    System.out.println(list);
  }
}

package com.tarena.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {
  
  public static String convertCurrentDate(String pattern){
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    return sdf.format(new Date());
  }
  
  public static Date addSecond(Date date, int second){
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.SECOND, second);
    return c.getTime();
  }
  
  public static void main(String[] args) {
    DateFormatUtil.showCurrentDate();
    //System.out.println(DateFormatUtil.convertCurrentDate("yyyy-mm-dd hh:mm:ss.SSS"));
    //System.out.println(DateFormatUtil.convertCurrentDate("yyyymmddhhmmssSSS"));
    //System.out.println(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(DateFormatUtil.addSecond(new Date(), 50)));
  }

  private static void showCurrentDate() {
    Calendar c = Calendar.getInstance();
    System.out.println(Calendar.DAY_OF_YEAR);
    
    System.out.println(Calendar.getInstance().getTime().toString());
    System.out.println(c.get(Calendar.MONTH));
    System.out.println(c.get(Calendar.DAY_OF_WEEK));
    System.out.println(c.get(Calendar.DAY_OF_YEAR));
  }
}

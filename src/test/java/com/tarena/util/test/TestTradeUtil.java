package com.tarena.util.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestTradeUtil {
  public static void main(String[] args) {
    String dates = "1,2,3,4,5,6,7";
    String times = "00:00-07:00,04:00-07:00,04:00-07:00,04:00-10:00,04:00-07:00,04:00-23:59,00:00-23:59";
    
    
 // String[] weeks = dates.split(",");
    String[] unAvailableTimes = times.split(",");

    Calendar c = Calendar.getInstance();
    //c.set(2017, 2, 19);
    //c.set(2017, 2, 20, 23, 59, 0);
    //c.add(Calendar.SECOND, 59);
    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    System.out.println(dayOfWeek);
    if (dayOfWeek == 1) {
      dayOfWeek = 6; // 数组6为星期天
    } else {
      dayOfWeek = dayOfWeek - 2; // 2-7 对应 数组0-5
    }
    SimpleDateFormat hmSDF = new SimpleDateFormat("HHmm");
    String[] unAvailableRange = unAvailableTimes[dayOfWeek].split("-");
    int endtime = Integer.parseInt(unAvailableRange[0].replace(":", ""));
    int starttime = Integer.parseInt(unAvailableRange[1].replace(":", ""));
    System.out.println(endtime);
    System.out.println(starttime);
    Date date = new Date(System.currentTimeMillis());
    int nowtime = Integer.parseInt(hmSDF.format(date));
    System.out.println(nowtime);
    if (nowtime >= endtime && nowtime <= starttime) {
//      return false;
      System.out.println("该商品" + unAvailableTimes[dayOfWeek]+"不可交易");
    } else {
      System.out.println(true); 
    }
  }
}

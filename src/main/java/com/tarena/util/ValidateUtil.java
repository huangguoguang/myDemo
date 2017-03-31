package com.tarena.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

  /**
   * 判断map 中的键是否为空，为空则返回false
   * @param map
   * @param str
   * @return
   */
  public static boolean checkIsBlank(Map<String,Object> map,String... str){
    boolean isBlank = true;
    for(int i = 0 ;i<str.length;i++){
      if(map.get(str[i]) == null || map.get(str[i]).equals("")){
        isBlank = false;
        break;
      }
    }
    return isBlank;
  }
  
  //判断是否是合法的手机号
  public static boolean isPhoneLegal(String str){
    return isChinaPhoneLegal(str) || isHongKongPhoneLegal(str);
  }

  
  /**
   * 香港手机号码5｜6｜8｜9开头加任意7位数
   * @param str
   * @return
   */
  public static boolean isHongKongPhoneLegal(String str) {
    String regExp = "^(5|6|8|9)\\d{7}$";
    Pattern p = Pattern.compile(regExp);
    Matcher m = p.matcher(str);
    return m.matches();
  }


  /** 
   * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数 
   * 此方法中前三位格式有： 
   * 13+任意数 
   * 15+除4的任意数 
   * 18+除1和4的任意数 
   * 17+除9的任意数 
   * 147 
   */  
  public static boolean isChinaPhoneLegal(String str) {
    String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
    Pattern p = Pattern.compile(regExp);
    Matcher m = p.matcher(str);
    return m.matches();
  }
}

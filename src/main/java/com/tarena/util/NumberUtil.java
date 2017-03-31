package com.tarena.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/** 
 * @author: chenwei
 * @version：1.0 
 * @创建时间：2016年8月30日 上午9:48:47 
 * @说明：四舍五入保留两位有效数字
 *
 */
public class NumberUtil {
	
	public static double fotmatNum(String str){
		 BigDecimal bd = new BigDecimal(Double.parseDouble(str)); 
		 return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static double fotmatNum(Double dd,int length){
		BigDecimal bd = new BigDecimal(dd); 
		 return bd.setScale(length, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static double ROUND_DOWN(int newScale, double data) {
		return new BigDecimal(data).setScale(newScale, BigDecimal.ROUND_DOWN).doubleValue();
	}
	
	public static void main(String[] args) {  
      String str="499.00022";  
//      BigDecimal bd = new BigDecimal(Double.parseDouble(str));  
//      System.out.println(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());  
//      System.out.println("=================");  
//       DecimalFormat df = new DecimalFormat("#.00");   
//       System.out.println(df.format(Double.parseDouble(str)));   
//       System.out.println("=================");  
//       System.out.println(String.format("%.2f", Double.parseDouble(str)));  
//       System.out.println("=================");  
//       NumberFormat nf = NumberFormat.getNumberInstance();   
//       nf.setMaximumFractionDigits(2);   
//       System.out.println(nf.format(Double.parseDouble(str)));   
      System.out.println(NumberUtil.fotmatNum(122.32323423423, 3));
  }  
}

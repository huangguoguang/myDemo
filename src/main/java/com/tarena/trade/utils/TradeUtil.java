package com.tarena.trade.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.tarena.exception.ValidationFailureException;
import com.tarena.trade.entity.BinaryOptionsBuyOrder;
import com.tarena.trade.entity.BinaryOptionsSellOrder;
import com.tarena.trade.entity.HangingDelistedBuyOrder;
import com.tarena.trade.entity.HangingDelistedSellOrder;
import com.tarena.trade.entity.StopProfitStopLossMongodb;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.entity.TradeSellOrderMongodb;
import com.tarena.trade.enums.EnumsTradeDirection;

public class TradeUtil {
	//
	// 交易
	//
	// 交易模式-挂摘牌
	public final static String Rule_HangingDelisted = "01";
	// 交易模式-固定收益
	public final static String Rule_BinaryOptions = "02";
  // 交易模式-点位期权
  public final static String Rule_PointOptions = "03";	
  //交易模式-众筹
  public final static String Rule_Crowdfunding = "04";  
  
  public final static String Rule_Crowdfunding_Step1 = "04S1";  
  
  public final static String Rule_Crowdfunding_Step2 = "04S2";  

	//
	// 挂摘牌平仓类型
	//
	public final static String OFFSET_BOOM = "BOOM";
	public final static String OFFSET_FORCE = "FORCE";
	public final static String OFFSET_AT = "AT";
	public final static String OFFSET_MT = "MT";

	public final static String OFFSET_MT_STOPPROFIT = "MT_STOPPROFIT";
	public final static String OFFSET_MT_STOPPLOSS = "MT_STOPLOSS";
	public final static String OFFSET_AT_STOPPROFIT = "MT_STOPPROFIT";
	public final static String OFFSET_AT_STOPPLOSS = "AT_STOPLOSS";

	//
	// MongoDB 表名
	//
	// 建仓表
	public final static String Mongodb_Table_TradeBuyOrder = "TradeBuyOrderMongodb";
	// 平仓表
	public final static String Mongodb_Table_TradeSellOrder = "TradeSellOrderMongodb";
	// 挂摘牌建仓表
	public final static String Mongodb_Table_HangingDelistedBuyOrder = "HangingDelistedBuyOrder";
	// 固定收益建仓表
	public final static String Mongodb_Table_BinaryOptionsBuyOrder = "BinaryOptionsBuyOrder";
	// 挂摘牌平仓表
	public final static String Mongodb_Table_HangingDelistedSellOrder = "HangingDelistedSellOrder";
	// 固定收益平仓表
	public final static String Mongodb_Table_BinaryOptionsSellOrder = "BinaryOptionsSellOrder";
	// 用户流水表
	public final static String Mongodb_Table_UserProfitLoss = "UserProfitLoss";
	// 经纪人流水表
	public final static String Mongodb_Table_BrokerProfitLoss = "BrokerProfitLoss";
	// 坏单
	public final static String Mongodb_Table_Bad_Order = "t_wp_bad_user_profitloss";
	
	
	public final static String Mongodb_Table_User = "t_wp_front_user_login";
	
	public final static String Mongodb_Table_Broker = "t_wp_front_broker";

	// 止盈止损
	public final static String Mongodb_Table_StopProfitStopLoss = "StopProfitStopLossMongodb";

	private static SimpleDateFormat hmSDF = new SimpleDateFormat("HHmm");

	public static boolean checkTradeDate(String dates, String times) throws ValidationFailureException{
		// String[] weeks = dates.split(",");
		String[] unAvailableTimes = times.split(",");

		Date date = new Date(System.currentTimeMillis());
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			dayOfWeek = 6; // 一周的第day_of_week是星期天，对应unAvaliableTimes[6]为星期天
		} else {
			dayOfWeek = dayOfWeek - 2; // day_of_week 2-7 对应 数组[0-5] 即值为2是星期一，数组的第0项
		}

		String[] unAvailableRange = unAvailableTimes[dayOfWeek].split("-");
		int endtime = Integer.parseInt(unAvailableRange[0].replace(":", ""));
		int starttime = Integer.parseInt(unAvailableRange[1].replace(":", ""));
		int nowtime = Integer.parseInt(hmSDF.format(date));
		if (nowtime >= endtime && nowtime <= starttime) {
//			return false;
			throw new ValidationFailureException("该商品" + unAvailableTimes[dayOfWeek] +"不可交易");
		} else {
			return true;
		}
	}
	
	
	public static boolean checkTradeDateAddSecond(String dates, String times, int second) throws ValidationFailureException{
		// String[] weeks = dates.split(",");
		String[] unAvailableTimes = times.split(",");

		Date date = new Date(System.currentTimeMillis());
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, second); // 当前时间+延期时间
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			dayOfWeek = 6; // 数组6为星期天
		} else {
			dayOfWeek = dayOfWeek - 2; // 2-7 对应 数组0-5
		}

		String[] unAvailableRange = unAvailableTimes[dayOfWeek].split("-");
		int endtime = Integer.parseInt(unAvailableRange[0].replace(":", ""));
		int starttime = Integer.parseInt(unAvailableRange[1].replace(":", ""));
		int nowtime = Integer.parseInt(hmSDF.format(date));
		if (nowtime >= endtime && nowtime <= starttime) {
//			return false;
			throw new ValidationFailureException("该商品" + unAvailableTimes[dayOfWeek] +"不可交易");
		} else {
			return true;
		}
	}

//	private static TradeBuyOrderMongodb convertBuyOrderForMongodb(BinaryOptionsBuyOrder origin) {
//		TradeBuyOrderMongodb buyOrder = new TradeBuyOrderMongodb();
//		buyOrder.setId(origin.getGdsy_order_id()); // 建仓ID
//		buyOrder.setOrder_id(origin.getGdsy_order_id()); // 建仓ID
//		// 层级
//		buyOrder.setAgent_id(origin.getAgent_id());
//		buyOrder.setBroker_id(origin.getBroker_id());
//		buyOrder.setUser_id(origin.getUser_id());
//
//		buyOrder.setBuy_type(origin.getGdsy_buy_type()); //
//		buyOrder.setBuy_point(origin.getGdsy_buy_point());
//		buyOrder.setBuy_time(origin.getGdsy_buy_time());
//		buyOrder.setBuy_itemcode(origin.getGdsy_buy_itemcode());
//		buyOrder.setBuy_itemname(origin.getGdsy_buy_itemname());
//		buyOrder.setBuy_brokerage(origin.getGdsy_buy_brokerage());
//		buyOrder.setBuy_amount(origin.getGdsy_buy_amount());
//		buyOrder.setBuy_all_price(origin.getGdsy_buy_all_price());
//		buyOrder.setBuy_price(origin.getGdsy_buy_price());
//		buyOrder.setConfirm(origin.getConfirm());
//		// 二元期权
//		buyOrder.setTrading_rule(TradeUtil.Rule_BinaryOptions);
//		return buyOrder;
//	}
//
//	private static TradeBuyOrderMongodb convertBuyOrderForMongodb(HangingDelistedBuyOrder origin) {
//		TradeBuyOrderMongodb buyOrder = new TradeBuyOrderMongodb();
//		buyOrder.setId(origin.getGzp_order_id()); // 建仓ID
//		buyOrder.setOrder_id(origin.getGzp_order_id()); // 建仓ID
//		// 层级
//		buyOrder.setAgent_id(origin.getAgent_id());
//		buyOrder.setBroker_id(origin.getBroker_id());
//		buyOrder.setUser_id(origin.getUser_id());
//
//		buyOrder.setBuy_type(origin.getGzp_buy_type()); //
//		buyOrder.setBuy_point(origin.getGzp_buy_point());
//		buyOrder.setBuy_time(origin.getGzp_buy_time());
//		buyOrder.setBuy_itemcode(origin.getGzp_buy_itemcode());
//		buyOrder.setBuy_itemname(origin.getGzp_buy_itemname());
//		buyOrder.setBuy_brokerage(origin.getGzp_buy_brokerage());
//		buyOrder.setBuy_amount(origin.getGzp_buy_amount());
//		buyOrder.setBuy_all_price(origin.getGzp_buy_all_price());
//		buyOrder.setBuy_price(origin.getGzp_buy_price());
//		buyOrder.setConfirm(origin.getConfirm());
//		// 挂摘牌
//		buyOrder.setTrading_rule(TradeUtil.Rule_HangingDelisted);
//		return buyOrder;
//
//	}

	public static BinaryOptionsSellOrder convertSellOrderForBinaryOptions(TradeSellOrderMongodb origin) {
		BinaryOptionsSellOrder sellOrder = new BinaryOptionsSellOrder();
		sellOrder.setGdsy_order_id(origin.getOrder_id());

		sellOrder.setP_id(origin.getP_id());
		sellOrder.setCh_id(origin.getCh_id());
		sellOrder.setCe_id(origin.getCe_id());
		sellOrder.setSettle_id(origin.getSettle_id());
		sellOrder.setAgent_id(origin.getAgent_id());
		sellOrder.setDept_id(origin.getDept_id());
		sellOrder.setBroker_id(origin.getBroker_id());
		sellOrder.setUser_id(origin.getUser_id());

		sellOrder.setGdsy_buy_all_price(origin.getBuy_all_price());
		sellOrder.setGdsy_buy_amount(origin.getBuy_amount());
		sellOrder.setGdsy_buy_brokerage(origin.getBuy_brokerage());
		sellOrder.setGdsy_buy_itemcode(origin.getBuy_itemcode());
		sellOrder.setGdsy_buy_point(origin.getBuy_point());
		sellOrder.setGdsy_buy_price(origin.getBuy_price());
		sellOrder.setGdsy_buy_time(origin.getBuy_time());
		sellOrder.setGdsy_buy_type(origin.getBuy_type());

		sellOrder.setGdsy_buy_ratio(origin.getGdsy_buy_ratio());

		sellOrder.setGdsy_sell_type(origin.getSell_type()); // 平仓类型
		sellOrder.setGdsy_sell_point(origin.getSell_point()); // 平仓点位
		sellOrder.setGdsy_sell_time(origin.getSell_time()); // 平仓时间

		sellOrder.setIs_use(origin.getIs_use());
		sellOrder.setRule(origin.getRule());
		return sellOrder;
	}

	public static HangingDelistedSellOrder convertSellOrderForHangingDelisted(TradeSellOrderMongodb origin) {
		HangingDelistedSellOrder sellOrder = new HangingDelistedSellOrder();
		sellOrder.setGzp_order_id(origin.getOrder_id());

		sellOrder.setP_id(origin.getP_id());
		sellOrder.setCh_id(origin.getCh_id());
		sellOrder.setCe_id(origin.getCe_id());
		sellOrder.setSettle_id(origin.getSettle_id());
		sellOrder.setAgent_id(origin.getAgent_id());
		sellOrder.setDept_id(origin.getDept_id());
		sellOrder.setBroker_id(origin.getBroker_id());
		sellOrder.setUser_id(origin.getUser_id());

		sellOrder.setGzp_buy_point(origin.getBuy_point());
		sellOrder.setGzp_buy_time(origin.getBuy_time());

		sellOrder.setGzp_buy_type(origin.getBuy_type());
		sellOrder.setGzp_buy_itemcode(origin.getBuy_itemcode());
		sellOrder.setGzp_buy_price(origin.getBuy_price());
		sellOrder.setGzp_buy_amount(origin.getBuy_amount());
		sellOrder.setGzp_buy_brokerage(origin.getBuy_brokerage());
		sellOrder.setGzp_buy_all_price(origin.getBuy_all_price());

		sellOrder.setGzp_sell_type(origin.getSell_type());// 平仓类型
		sellOrder.setGzp_sell_point(origin.getSell_point()); // 平仓点位
		sellOrder.setGzp_sell_time(origin.getSell_time()); // 平仓时间

		sellOrder.setIs_use(origin.getIs_use());
		
		sellOrder.setGzp_sell_zy_point(origin.getGzp_sell_zy_point());
		sellOrder.setGzp_sell_zs_point(origin.getGzp_sell_zs_point());
		
		// 众筹
		sellOrder.setZc_order_id(origin.getZc_order_id());
		sellOrder.setIs_zc(origin.getIs_zc());
		return sellOrder;
	}

	public static BinaryOptionsBuyOrder convertBuyOrderForBinaryOptions(TradeBuyOrderMongodb origin) {
		BinaryOptionsBuyOrder buyOrder = new BinaryOptionsBuyOrder();
		buyOrder.setGdsy_order_id(origin.getOrder_id());
		buyOrder.setAgent_id(origin.getAgent_id());
		buyOrder.setBroker_id(origin.getBroker_id());
		buyOrder.setUser_id(origin.getUser_id());

		buyOrder.setGdsy_buy_all_price(origin.getBuy_all_price());
		buyOrder.setGdsy_buy_itemcode(origin.getBuy_itemcode());
		buyOrder.setGdsy_buy_itemname(origin.getBuy_itemname());
		buyOrder.setGdsy_buy_price(origin.getBuy_price());
		buyOrder.setGdsy_buy_brokerage(origin.getBuy_brokerage());

		buyOrder.setGdsy_buy_type(origin.getBuy_type());
		buyOrder.setGdsy_buy_point(origin.getBuy_point());
		buyOrder.setGdsy_buy_time(origin.getBuy_time());
		buyOrder.setGdsy_buy_amount(origin.getBuy_amount());

		buyOrder.setIs_use(origin.getIs_use());
		buyOrder.setConfirm(origin.getConfirm());

		buyOrder.setGdsy_buy_xz_time(origin.getGdsy_buy_xz_time());
		buyOrder.setGdsy_buy_ratio(origin.getGdsy_buy_ratio());

		return buyOrder;
	}

	public static HangingDelistedBuyOrder convertBuyOrderForHangingDelisted(TradeBuyOrderMongodb origin) {
		HangingDelistedBuyOrder buyOrder = new HangingDelistedBuyOrder();
		buyOrder.setGzp_order_id(origin.getOrder_id());

		buyOrder.setAgent_id(origin.getAgent_id());
		buyOrder.setBroker_id(origin.getBroker_id());
		buyOrder.setUser_id(origin.getUser_id());

		buyOrder.setGzp_buy_price(origin.getBuy_price());
		buyOrder.setGzp_buy_amount(origin.getBuy_amount());
		buyOrder.setGzp_buy_brokerage(origin.getBuy_brokerage());
		buyOrder.setGzp_buy_all_price(origin.getBuy_all_price());
		buyOrder.setGzp_buy_itemcode(origin.getBuy_itemcode());
		buyOrder.setGzp_buy_itemname(origin.getBuy_itemname());

		buyOrder.setGzp_buy_type(origin.getBuy_type());
		buyOrder.setGzp_buy_point(origin.getBuy_point());
		buyOrder.setGzp_buy_time(origin.getBuy_time());

		buyOrder.setIs_use(origin.getIs_use());
		buyOrder.setConfirm(origin.getConfirm());
		buyOrder.setPoint(origin.getPoint());
		// 众筹
		buyOrder.setZc_order_id(origin.getZc_order_id());
		buyOrder.setIs_zc(origin.getIs_zc());
		return buyOrder;
	}

	public static TradeSellOrderMongodb convertBuyOrderMongodb(TradeBuyOrderMongodb origin) {
		TradeSellOrderMongodb sellMongodb = new TradeSellOrderMongodb();
//		sellMongodb.setId(origin.getOrder_id()); // 平仓ID
		sellMongodb.setOrder_id(origin.getOrder_id());
		// 层级
		sellMongodb.setP_id(origin.getP_id());
		sellMongodb.setCh_id(origin.getCh_id());
		sellMongodb.setCe_id(origin.getCe_id());
		sellMongodb.setSettle_id(origin.getSettle_id());
		sellMongodb.setSettle_name(origin.getSettle_name());
		sellMongodb.setAgent_id(origin.getAgent_id());
		sellMongodb.setAgent_name(origin.getAgent_name());
		sellMongodb.setDept_id(origin.getDept_id());
		sellMongodb.setDept_name(origin.getDept_name());
		sellMongodb.setBroker_id(origin.getBroker_id());
		sellMongodb.setBroker_name(origin.getBroker_name());
		sellMongodb.setUser_id(origin.getUser_id());
		sellMongodb.setUser_name(origin.getUser_name());
		sellMongodb.setUser_mobile(origin.getUser_mobile());

		sellMongodb.setBuy_type(origin.getBuy_type());
		sellMongodb.setBuy_point(origin.getBuy_point());
		sellMongodb.setBuy_unit(origin.getBuy_unit());
		sellMongodb.setBuy_time(origin.getBuy_time());
		sellMongodb.setBuy_itemtype(origin.getBuy_itemtype());
		sellMongodb.setBuy_itemcode(origin.getBuy_itemcode());
		sellMongodb.setBuy_itemname(origin.getBuy_itemname());
		sellMongodb.setBuy_brokerage(origin.getBuy_brokerage());
		sellMongodb.setBuy_amount(origin.getBuy_amount());
		sellMongodb.setBuy_all_price(origin.getBuy_all_price());
		sellMongodb.setBuy_price(origin.getBuy_price());
		sellMongodb.setIs_use(origin.getIs_use());
		sellMongodb.setCreate_date(origin.getCreate_date());
		// 挂摘牌
		sellMongodb.setTrading_rule(origin.getTrading_rule());
		sellMongodb.setGzp_profit(origin.getGzp_profit());
		sellMongodb.setGzp_sell_zy_point(origin.getGzp_sell_zy_point());
		sellMongodb.setGzp_sell_zs_point(origin.getGzp_sell_zs_point());
		// 固定收益
		sellMongodb.setGdsy_buy_ratio(origin.getGdsy_buy_ratio());
		sellMongodb.setGdsy_buy_xz_time(origin.getGdsy_buy_xz_time());
		// 商品ID
		sellMongodb.setItem_id(origin.getItem_id());
		// 点位期权
		sellMongodb.setPoint(origin.getPoint());
		
		sellMongodb.setBuy_itemname_alias(origin.getBuy_itemname_alias()); // 别名
		
		// 众筹步骤
		sellMongodb.setStep(origin.getStep());
		sellMongodb.setZc_order_id(origin.getZc_order_id());
		sellMongodb.setIs_zc(origin.getIs_zc());
		
		sellMongodb.setBuy_number(origin.getBuy_number());
		return sellMongodb;
	}

	public static StopProfitStopLossMongodb convertBuyOrderForStopLoss(TradeBuyOrderMongodb origin) {
		StopProfitStopLossMongodb stopp = new StopProfitStopLossMongodb();
//		stopp.setId(origin.getOrder_id());
		stopp.setOrder_id(origin.getOrder_id());
		stopp.setUser_id(origin.getUser_id());
		stopp.setBuy_itemcode(origin.getBuy_itemcode());
		stopp.setBuy_itemtype(origin.getBuy_itemtype());
		stopp.setBuy_point(origin.getBuy_point());
		stopp.setBuy_type(origin.getBuy_type());
		stopp.setGzp_sell_zs_point(origin.getGzp_sell_zs_point());
		stopp.setGzp_sell_zy_point(origin.getGzp_sell_zy_point());
		stopp.setConfirm("1");
		stopp.setPoint(origin.getPoint());
		return stopp;
	}
	
	public static TradeBuyOrderMongodb convertSellOrderForCrowdfunding(TradeSellOrderMongodb origin){
		TradeBuyOrderMongodb buyOrder = new TradeBuyOrderMongodb();
		buyOrder.setOrder_id(IdentificationUtil.generateId()); // 建仓ID
		buyOrder.setZc_order_id(origin.getOrder_id()); // 众筹订单号
		buyOrder.setIs_zc("1");
		// 层级
		buyOrder.setP_id(origin.getP_id());
		buyOrder.setCh_id(origin.getCh_id());
		buyOrder.setCe_id(origin.getCe_id());
		buyOrder.setSettle_id(origin.getSettle_id()); // 结算
		buyOrder.setSettle_name(origin.getSettle_name());
		buyOrder.setAgent_id(origin.getAgent_id()); // 代理商ID
		buyOrder.setAgent_name(origin.getAgent_name());
		buyOrder.setDept_id(origin.getDept_id()); // 部门
		buyOrder.setDept_name(origin.getDept_name());
		buyOrder.setBroker_id(origin.getBroker_id()); // 经纪人ID
		buyOrder.setBroker_name(origin.getBroker_name());
		buyOrder.setUser_id(origin.getUser_id()); // 用户ID
		buyOrder.setUser_name(origin.getUser_name());
		buyOrder.setUser_mobile(origin.getUser_mobile()); // 手机号

		buyOrder.setBuy_type(origin.getBuy_type()); // 方向
		buyOrder.setBuy_point(origin.getSell_point()); // 建仓点位
		buyOrder.setBuy_time(new Date()); // 建仓时间
		buyOrder.setBuy_itemtype(origin.getBuy_itemtype()); // 商品数型
		buyOrder.setBuy_itemcode(origin.getBuy_itemcode()); // 商品CODE
		buyOrder.setBuy_itemname(origin.getBuy_itemname()); // 商品名称
		buyOrder.setBuy_number(origin.getBuy_number()); // 商品数型
		buyOrder.setBuy_unit(origin.getBuy_unit()); // 单位
		buyOrder.setBuy_price(origin.getBuy_price()); // 单笔金额
		buyOrder.setBuy_amount(origin.getBuy_amount() * 2); // 手数
		buyOrder.setBuy_brokerage(0); // 总手续费
		buyOrder.setBuy_all_price(origin.getSettlement_value()); // 订单金额
		buyOrder.setGzp_profit(origin.getGzp_profit()); // 波动盈亏
		buyOrder.setConfirm("2"); // 建仓中
		buyOrder.setIs_use("1"); // 可用
		buyOrder.setItem_id(origin.getItem_id()); // 商品ID
		buyOrder.setStep(origin.getStep());
		// 挂摘牌
		buyOrder.setTrading_rule(TradeUtil.Rule_HangingDelisted);

		buyOrder.setBuy_itemname_alias(origin.getBuy_itemname_alias()); // 别名
		
		
		double zsPoint = 100; // 默认止损点
		if (buyOrder.getBuy_itemtype().contains("cu")) {
			zsPoint = 1000; // 铜默认止损点
		}else if (buyOrder.getBuy_itemtype().contains("mo")) {
			zsPoint = 50; // 美蓝莓默认止损点
		}
		if (EnumsTradeDirection.BUY.match(buyOrder.getBuy_type())) {
			buyOrder.setGzp_sell_zs_point(buyOrder.getBuy_point() - zsPoint);
		} else {
			buyOrder.setGzp_sell_zs_point(buyOrder.getBuy_point() + zsPoint);
		}
		return buyOrder;
	}


  /**
   * 根据点位类型min和图表类型type构造redisKey
   * @param type
   * @param min
   * @return
   */
  public static String convertLineKey(String type, String min) {
    Set<String> types = new HashSet<String>();
    types.add("6");
    types.add("10");
    types.add("12");
    types.add("13");
    types.add("14");
    Set<String> mins = new HashSet<String>();
    mins.add("-1");
    mins.add("1");
    mins.add("5");
    mins.add("15");
    mins.add("30");
    mins.add("60");
    mins.add("0");
    
    String redisLineKey = "";
    if(types.contains(type)  && mins.contains(min)){
      if("-1".equals(min)){
        redisLineKey = "type" + type +"line1";
      }else {
        redisLineKey = "type" + type +"m" + min;
      }
    }
    return redisLineKey;
  }
}

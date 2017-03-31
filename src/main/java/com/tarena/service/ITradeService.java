package com.tarena.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.tarena.common.component.session.SessionProvider;
import com.tarena.util.Pagination;

public interface ITradeService {

  /**用户每种商品的交易手数
   * @param session
   * @return
   */
  Object queryUserLot(SessionProvider session);

  Object totalOrder(SessionProvider session);

  /**
   * 查询未平仓订单
   * @param user_id
   * @return
   */
  @SuppressWarnings("rawtypes")
  public List<Map> queryBuyOrder(String user_id) throws ParseException;

  /**
   * 查询已平仓订单
   * @param user_id
   * @param firstDate
   * @param lastDate
   * @param string
   * @param page
   * @param pageSize
   * @param collection
   * @return
   * @throws ParseException
   */
  @SuppressWarnings("rawtypes")
  public Pagination<Map> querySellOrder(String user_id, String firstDate, String lastDate,
      String string, int page, int pageSize, String collection) throws ParseException;

  /**查询众筹订单
   * @param user_id
   * @return
   * @throws ParseException 
   */
  @SuppressWarnings("rawtypes")
  public List<Map> queryCrowdfundingBuyOrder(String user_id) throws ParseException;

}

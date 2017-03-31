package com.tarena.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tarena.entity.UserProfitLoss;
import com.tarena.entity.UserWallet;
import com.tarena.exception.BusinessException;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.entity.TradeSellOrderMongodb;

public interface IProcessOrderService {

  //根据user_id查询用户钱包信息
  UserWallet queryUserWalletById(String user_id);


  /**
   * 处理建仓订单
   **/
  @Transactional(rollbackFor={BusinessException.class},propagation = Propagation.REQUIRED)
  boolean processBuyOrder(String trading_rule,
      Object buyOrder,UserProfitLoss userProfitLoss, UserWallet userWallet) throws BusinessException;


  /**建仓订单确认
   * @param buyOrder
   * @param userProfitLoss
   * @return
   */
  boolean buyConfirm(TradeBuyOrderMongodb buyOrder, UserProfitLoss userProfitLoss);

  /**
   * 建仓订单回滚
   * 
   */
  boolean buyRollBack(TradeBuyOrderMongodb buyOrder);

  /**
   * 建仓失败写坏单表
   * 
   * */
  boolean insertBadBuyOrder(TradeBuyOrderMongodb buyOrder);


  /**
   * 处理平仓订单
   * @param ruleBinaryoptions
   * @param boSellOrder
   * @param userProfitLoss
   * @param userWallet
   * @return
   */
  @Transactional(rollbackFor={BusinessException.class},propagation = Propagation.REQUIRED)
  boolean processSellOrder(String tradingRules,
      Object sellOrder, UserProfitLoss userProfitLoss,
      UserWallet userWallet) throws BusinessException;


  /**
   * 平仓订单确认
   * @param sellOrder
   * @param userProfitLoss
   * @return
   */
  boolean  sellConfirm(TradeSellOrderMongodb sellOrder,
      UserProfitLoss userProfitLoss);
  

  /**
   * 平仓订单回滚
   * 
   */
  boolean sellRollBack(TradeSellOrderMongodb sellOrder);

  /**
   * 平仓失败写坏单表
   * 
   * */
  boolean insertBadSellOrder(TradeSellOrderMongodb sellOrder);

}

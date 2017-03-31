package com.tarena.service.impl;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarena.dao.IProcessOrderDao;
import com.tarena.entity.UserProfitLoss;
import com.tarena.entity.UserWallet;
import com.tarena.exception.BusinessException;
import com.tarena.lock.DistributeLock;
import com.tarena.service.IProcessOrderService;
import com.tarena.trade.entity.BinaryOptionsBuyOrder;
import com.tarena.trade.entity.BinaryOptionsSellOrder;
import com.tarena.trade.entity.HangingDelistedBuyOrder;
import com.tarena.trade.entity.HangingDelistedSellOrder;
import com.tarena.trade.entity.TradeBadOrder;
import com.tarena.trade.entity.TradeBuyOrderMongodb;
import com.tarena.trade.entity.TradeSellOrderMongodb;
import com.tarena.trade.listener.TradeProcess;
import com.tarena.trade.utils.IdentificationUtil;
import com.tarena.trade.utils.TradeUtil;

@Service
public class ProcessOrderServiceImpl implements IProcessOrderService {

  @Autowired
  private IProcessOrderDao processOrderDao;
  @Autowired
  private DistributeLock distributeLock;
  @Autowired
  private TradeProcess tradeProcess;
  @Override
  public UserWallet queryUserWalletById(String user_id) {
    return processOrderDao.queryUserWalletById(user_id);
  }

  /* 处理建仓订单
   * @see com.tarena.service.IProcessOrderService#processBuyOrder(java.lang.String, com.tarena.trade.entity.HangingDelistedBuyOrder, com.tarena.entity.UserProfitLoss, com.tarena.entity.UserWallet)
   */
  @Override
  public boolean processBuyOrder(String trading_rule,
      Object buyOrder,
      UserProfitLoss userProfitLoss, UserWallet userWallet) throws BusinessException {
    //加用户钱包锁
    String lockMysqlWallet = distributeLock.getLock("mysql_wallet", String.valueOf(userWallet.getUser_id()));
    if(lockMysqlWallet == null){
      return false;
    }
    try{
      if(trading_rule.equals(TradeUtil.Rule_HangingDelisted)){
        //处理挂摘牌建仓
        HangingDelistedBuyOrder order = (HangingDelistedBuyOrder) buyOrder;
        if(processOrderDao.insertHangingDelistedBuyOrder(order) != 1){
          throw new BusinessException("新增挂摘牌建仓、挂单订单失败");
        }
      }else if(trading_rule.equals(TradeUtil.Rule_BinaryOptions)){
        //处理固定收益建仓
        BinaryOptionsBuyOrder order = (BinaryOptionsBuyOrder) buyOrder;
        if(processOrderDao.insertBinaryOptionsBuyOrder(order) != 1){
          throw new BusinessException("新增固定收益建仓、挂单订单失败");
        }
      }else if(trading_rule.equals(TradeUtil.Rule_PointOptions)){
        //处理点位期权建仓
        HangingDelistedBuyOrder order = (HangingDelistedBuyOrder) buyOrder;
        if(processOrderDao.insertHangingDelistedBuyOrder(order) != 1){
          throw new BusinessException("新增点位期权订单失败!");
        }
      }else if(trading_rule.equals(TradeUtil.Rule_Crowdfunding)){
        //众筹建仓
        BinaryOptionsBuyOrder order = (BinaryOptionsBuyOrder) buyOrder;
        if(processOrderDao.insertBinaryOptionsBuyOrder(order) != 1){
          throw new BusinessException("新增众筹固定收益订单失败!");
        }
      }else{
        throw new BusinessException("不支持的交易模式");
      }
      //写流水
      if(processOrderDao.insertUserProfitLoss(userProfitLoss) != 1){
        throw new BusinessException("新增用户流水失败");
      }
      //更新用户钱包
      UserWallet dbUserWallet = processOrderDao.queryUserWalletById(userWallet.getUser_id());
      if(dbUserWallet != null){
        BigDecimal userBalance = new BigDecimal(dbUserWallet.getUser_money());//用户余额
        BigDecimal orderMoney = new BigDecimal(userWallet.getOrder_money());//订单金额 
        
        if(orderMoney.compareTo(userBalance) == 1){
          throw new BusinessException("订单金额大于用户余额");
        }
        dbUserWallet.setOrder_money(orderMoney.doubleValue());
        dbUserWallet.setType(userWallet.getType());
        dbUserWallet.setWallet_id(userWallet.getWallet_id());
        
        if(processOrderDao.updateUserWallet(dbUserWallet) != 1){
          throw new BusinessException("更新用户钱包失败");
        }
      }else{
        throw new BusinessException("查无此用户钱包");
      }
    }catch(Exception e){
      throw new BusinessException("更新用户钱包失败",e);
    }finally{
      distributeLock.releaseLock("mysql_wallet", String.valueOf(userWallet.getUser_id()), lockMysqlWallet);
    }
    return true;
  }

  @Override
  public boolean buyConfirm(TradeBuyOrderMongodb buyOrder,
      UserProfitLoss userProfitLoss) {
    return tradeProcess.buyConfirm(buyOrder, userProfitLoss);
  }

  @Override
  public boolean buyRollBack(TradeBuyOrderMongodb buyOrder) {
    return tradeProcess.buyRollBack(buyOrder,this);
  }

  @Override
  public boolean insertBadBuyOrder(TradeBuyOrderMongodb buyOrder) {
    TradeBadOrder badOrder = new TradeBadOrder();
    badOrder.setId(IdentificationUtil.generateId());
    badOrder.setUser_id(badOrder.getUser_id());
    badOrder.setOrder_id(buyOrder.getOrder_id());
    badOrder.setBad_money(buyOrder.getBuy_all_price());
    badOrder.setBad_type("01");
    badOrder.setBad_time(buyOrder.getBuy_time());
    badOrder.setBad_status("1");
    if(TradeUtil.Rule_HangingDelisted.equals(buyOrder.getTrading_rule())){
      badOrder.setBad_desc("挂摘牌订单建仓失败,无需回退金额");
    }else if(TradeUtil.Rule_BinaryOptions.equals(buyOrder.getTrading_rule())){
      badOrder.setBad_desc("固定收益订单建仓失败,无需回退金额");
    }else if(TradeUtil.Rule_PointOptions.equals(buyOrder.getTrading_rule())){
      badOrder.setBad_desc("点位期权订单建仓失败，无需回退金额");
    }
    return processOrderDao.insertBadOrder(badOrder) == 1;
  }

  /*
   * 处理平仓订单
   */
  @Override
  public boolean processSellOrder(String tradingRules,Object sellOrder, UserProfitLoss userProfitLoss,UserWallet userWallet) throws BusinessException {
    String lockMysqlWallet = distributeLock.getLock("mysql_wallet", userWallet.getUser_id());
    if(lockMysqlWallet == null) {
      return false;
    }
    try{
      if(tradingRules.equals(TradeUtil.Rule_BinaryOptions)){
        //处理固定收益平仓
        BinaryOptionsSellOrder order = (BinaryOptionsSellOrder) sellOrder;
        if(processOrderDao.deleteBinaryOptionsBuyOrder(order.getGdsy_order_id()) != 1){
          throw new BusinessException("删除固定收益建仓订单失败!");
        }
        if(processOrderDao.insertBinaryOptionsSellOrder(order) != 1){
          throw new BusinessException("新增固定收益平仓订单失败");
        }
      }else if (StringUtils.equals(tradingRules, TradeUtil.Rule_HangingDelisted)) {
        //
        // 处理挂摘牌平仓、挂单平仓
        //
        HangingDelistedSellOrder order = (HangingDelistedSellOrder) sellOrder;

        if (processOrderDao.deleteHangingDelistedBuyOrder(order.getGzp_order_id()) != 1) {
          throw new BusinessException("删除挂摘牌建仓订单失败!");
        }
        if (processOrderDao.insertHangingDelistedSellOrder(order) != 1) {
          throw new BusinessException("新增挂摘牌平仓订单失败!");
        }
        // 爆仓的情况，订单金额为0，不插流水，不更新余额
        if (userWallet.getOrder_money() == 0) {
          return true;
        }
      }else {
        throw new BusinessException("无此交易模式");
      }
      //写流水
      if(processOrderDao.insertUserProfitLoss(userProfitLoss) != 1) {
        throw new BusinessException("新增用户流水失败!");
      }
      //更新用户钱包
      UserWallet dbUserWallet = processOrderDao.queryUserWalletById(userWallet.getUser_id());
      if(dbUserWallet != null) {
        BigDecimal orderMoney = new BigDecimal(userWallet.getOrder_money());//订单金额
        dbUserWallet.setOrder_money(orderMoney.doubleValue());
        dbUserWallet.setType(userWallet.getType());
        dbUserWallet.setWallet_id(userWallet.getWallet_id());
        if(processOrderDao.updateUserWallet(dbUserWallet) != 1){
          throw new BusinessException("用户钱包更新失败");
        }
      } else {
        throw new BusinessException("查无此用户钱包!");
      }
    } catch(Exception e){
      throw new BusinessException("更新用户钱包失败!", e);
    }finally{
      distributeLock.releaseLock("mysql_wallet", userWallet.getUser_id(), lockMysqlWallet);
    }
    return true;
  }

  @Override
  public boolean sellConfirm(TradeSellOrderMongodb sellOrder,
      UserProfitLoss userProfitLoss) {
    return tradeProcess.sellConfirm(sellOrder, userProfitLoss);
  }

  @Override
  public boolean sellRollBack(TradeSellOrderMongodb sellOrder) {
    return tradeProcess.sellRollBack(sellOrder,this);
  }

  @Override
  public boolean insertBadSellOrder(TradeSellOrderMongodb sellOrder) {
    TradeBadOrder badOrder = new TradeBadOrder();
    badOrder.setId(IdentificationUtil.generateId());
    badOrder.setBad_money(sellOrder.getBuy_all_price());
    badOrder.setBad_status("0");
    badOrder.setBad_time(sellOrder.getBuy_time());
    badOrder.setBad_type("02");//平仓坏单表
    badOrder.setOrder_id(sellOrder.getOrder_id());
    badOrder.setUser_id(sellOrder.getUser_id());
    if(TradeUtil.Rule_BinaryOptions.equals(sellOrder.getTrading_rule())){
      badOrder.setBad_desc("固定收益订单平仓失败，需退回金额");
    } else if(TradeUtil.Rule_HangingDelisted.equals(sellOrder.getTrading_rule())){
      badOrder.setBad_desc("挂摘牌订单平仓失败，需退回金额");
    } else if(TradeUtil.Rule_PointOptions.equals(sellOrder.getTrading_rule())){
      badOrder.setBad_desc("点位期权订单平仓失败，需退回金额");
    }
    return processOrderDao.insertBadOrder(badOrder) == 1;
  }

}

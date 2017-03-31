package com.tarena.dao;

import com.tarena.entity.UserProfitLoss;
import com.tarena.entity.UserWallet;
import com.tarena.trade.entity.BinaryOptionsBuyOrder;
import com.tarena.trade.entity.BinaryOptionsSellOrder;
import com.tarena.trade.entity.HangingDelistedBuyOrder;
import com.tarena.trade.entity.HangingDelistedSellOrder;
import com.tarena.trade.entity.TradeBadOrder;

public interface IProcessOrderDao {
  /**
   * 查询用户钱包
   * 
   * @param wallet_id
   * 
   * */
  UserWallet queryUserWalletById(String user_id);

  /**
   * 新增挂摘牌建仓订单
   * @param order
   * @return
   */
  int insertHangingDelistedBuyOrder(HangingDelistedBuyOrder order);

  /**
   * 新增用户流水
   * @param userProfitLoss
   * @return
   */
  int insertUserProfitLoss(UserProfitLoss userProfitLoss);

  /**
   * 更新钱包
   * @param dbUserWallet
   * @return
   */
  int updateUserWallet(UserWallet dbUserWallet);

  /**
   * 平仓失败写坏单表
   * 
   * */
  int insertBadOrder(TradeBadOrder badOrder);

  /**新增固定收益建仓订单
   * @param order
   * @return
   */
  int insertBinaryOptionsBuyOrder(BinaryOptionsBuyOrder order);

  /**
   * 删除固定收益建仓订单
   **/
  int deleteBinaryOptionsBuyOrder(String gdsy_order_id);

  /**
   * 新增固定收益平仓订单
   **/
  int insertBinaryOptionsSellOrder(BinaryOptionsSellOrder order);

  /**
   * 删除挂摘牌建仓订单
   **/
  int deleteHangingDelistedBuyOrder(String gzp_order_id);
  /**
   * 新增挂摘牌平仓订单
   **/
  int insertHangingDelistedSellOrder(HangingDelistedSellOrder order);

}

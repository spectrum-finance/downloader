package fi.spectrum
package repositories.sql

import models.{Address, Order, PoolId}

import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.query.Query0

final class OrderSql(implicit lh: LogHandler) {

  def getRedeemByAddress(address: Address, poolId: PoolId, from: Long, to: Long): Query0[Order.Redeem] =
    sql"""
         |select order_id, pool_id, timestamp, output_amount_x, output_amount_y, redeemer from redeems
         |where redeemer = $address and pool_id = $poolId and timestamp > $from and timestamp < $to
         |order by timestamp
       """.stripMargin.query[Order.Redeem]

  def getDepositByAddress(address: Address, poolId: PoolId, from: Long, to: Long): Query0[Order.Deposit] =
    sql"""
         |select order_id, pool_id, timestamp, input_amount_x, input_amount_y, redeemer from deposits
         |where redeemer = $address and pool_id = $poolId and timestamp > $from and timestamp < $to
         |order by timestamp
       """.stripMargin.query[Order.Deposit]
}

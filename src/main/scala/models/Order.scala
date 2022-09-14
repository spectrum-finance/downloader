package fi.spectrum
package models

sealed trait Order {
  val timestamp: Long
}

object Order {

  final case class Redeem(
    orderId: OrderId,
    poolId: PoolId,
    timestamp: Long,
    outputAmountX: Long,
    outputAmountY: Long,
    address: Address
  ) extends Order

  final case class Deposit(
    orderId: OrderId,
    poolId: PoolId,
    timestamp: Long,
    inputAmountX: Long,
    inputAmountY: Long,
    address: Address
  ) extends Order
}

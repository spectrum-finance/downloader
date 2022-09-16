package fi.spectrum
package models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

@derive(encoder, decoder, loggable)
sealed trait Order {
  val timestamp: Long
}

object Order {

  @derive(encoder, decoder, loggable)
  final case class Redeem(
    orderId: OrderId,
    poolId: PoolId,
    timestamp: Long,
    lp: Long,
    address: Address
  ) extends Order

  @derive(encoder, decoder, loggable)
  final case class Deposit(
    orderId: OrderId,
    poolId: PoolId,
    timestamp: Long,
    lp: Long,
    address: Address
  ) extends Order
}

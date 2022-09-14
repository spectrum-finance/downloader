package fi.spectrum
package repositories

import models.{Address, Order, PoolId}

import scala.concurrent.duration.FiniteDuration

trait OrderRepository[F[_]] {
  def getByAddress(address: Address, poolId: PoolId, from: Long, to: Long): F[List[Order]]
}

package fi.spectrum
package repositories

import models.{PoolId, TokenId}

trait PoolsRepository[F[_]] {
  def getPoolInfo(poolId: PoolId): F[TokenId]
}

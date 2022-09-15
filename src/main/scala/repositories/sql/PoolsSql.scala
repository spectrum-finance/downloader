package fi.spectrum
package repositories.sql

import models.{PoolId, TokenId}

import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.query.Query0

final class PoolsSql(implicit lh: LogHandler) {
  def getPoolInfo(poolId: PoolId): Query0[TokenId] =
    sql"""select x_id from pools where pool_id = $poolId limit 1;""".query[TokenId]
}

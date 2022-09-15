package fi.spectrum
package repositories.sql

import models.{Address, PoolId}

import cats.syntax.foldable._
import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.query.Query0

final class AddressSql(implicit lh: LogHandler) {

  def getAll(pools: List[PoolId], from: Long, to: Long): Query0[Address] =
    sql"""
         |select distinct redeemer from deposits
         |where timestamp > $from and timestamp < $to and pool_id in (${pools.map(n => fr"$n").intercalate(fr",")})
         |order by timestamp limit 1"""
      .query[Address]
}

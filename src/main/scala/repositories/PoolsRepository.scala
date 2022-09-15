package fi.spectrum
package repositories

import models.{PoolId, TokenId}
import repositories.sql.PoolsSql

import cats.{FlatMap, Functor}
import derevo.derive
import doobie.ConnectionIO
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr
import tofu.higherKind.Mid
import tofu.higherKind.derived.representableK
import tofu.logging.{Logging, Logs}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import cats.tagless.syntax.functorK._

@derive(representableK)
trait PoolsRepository[F[_]] {
  def getPoolInfo(poolId: PoolId): F[TokenId]
}

object PoolsRepository {

  def create[I[_]: Functor, D[_]: FlatMap: LiftConnectionIO, F[_]](implicit
    elh: EmbeddableLogHandler[D],
    logs: Logs[I, D],
    txr: Txr[F, D]
  ): I[PoolsRepository[F]] =
    logs.forService[PoolsRepository[F]].map { implicit __ =>
      elh
        .embed(implicit lh => new Tracing[D] attach new Live(new PoolsSql()).mapK(LiftConnectionIO[D].liftF))
        .mapK(txr.trans)
    }

  final private class Live(sql: PoolsSql) extends PoolsRepository[ConnectionIO] {
    def getPoolInfo(poolId: PoolId): ConnectionIO[TokenId] = sql.getPoolInfo(poolId).unique
  }

  final private class Tracing[F[_]: FlatMap: Logging] extends PoolsRepository[Mid[F, *]] {

    def getPoolInfo(poolId: PoolId): Mid[F, TokenId] =
      for {
        _ <- info"Going to get info about pool $poolId."
        r <- _
        _ <- info"Pool $poolId info is $r."
      } yield r
  }
}

package fi.spectrum
package repositories

import models.{Address, Order, PoolId}
import repositories.sql.OrderSql

import cats.{FlatMap, Functor}
import doobie.ConnectionIO
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr
import tofu.higherKind.Mid
import tofu.logging.{Logging, Logs}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import cats.tagless.syntax.functorK._
import derevo.derive
import tofu.higherKind.derived.representableK

@derive(representableK)
trait OrderRepository[F[_]] {
  def getByAddress(address: Address, poolId: PoolId, from: Long, to: Long): F[List[Order]]
}

object OrderRepository {

  def create[I[_]: Functor, D[_]: FlatMap: LiftConnectionIO, F[_]](implicit
    elh: EmbeddableLogHandler[D],
    logs: Logs[I, D],
    txr: Txr[F, D]
  ): I[OrderRepository[F]] =
    logs.forService[OrderRepository[F]].map { implicit __ =>
      elh
        .embed(implicit lh => new Tracing[D] attach new Live(new OrderSql()).mapK(LiftConnectionIO[D].liftF))
        .mapK(txr.trans)
    }

  final private class Live(sql: OrderSql) extends OrderRepository[ConnectionIO] {

    def getByAddress(address: Address, poolId: PoolId, from: Long, to: Long): ConnectionIO[List[Order]] =
      for {
        redeems  <- sql.getRedeemByAddress(address, poolId, from, to).to[List]
        deposits <- sql.getDepositByAddress(address, poolId, from, to).to[List]
      } yield (redeems prependedAll deposits).sortBy(_.timestamp)
  }

  final private class Tracing[F[_]: FlatMap: Logging] extends OrderRepository[Mid[F, *]] {

    def getByAddress(address: Address, poolId: PoolId, from: Long, to: Long): Mid[F, List[Order]] =
      for {
        _ <- info"Going to get all orders using address $address for pool $poolId from $from to $to."
        r <- _
        _ <- info"Got orders for address $address and pool $poolId $r."
      } yield r
  }
}

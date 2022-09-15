package fi.spectrum
package repositories

import models.{Address, PoolId}
import repositories.sql.AddressSql

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
trait AddressRepository[D[_]] {
  def getAll(pools: List[PoolId], from: Long, to: Long): D[List[Address]]
}

object AddressRepository {

  def create[I[_]: Functor, D[_]: FlatMap: LiftConnectionIO, F[_]](implicit
    elh: EmbeddableLogHandler[D],
    logs: Logs[I, D],
    txr: Txr[F, D]
  ): I[AddressRepository[F]] =
    logs.forService[AddressRepository[F]].map { implicit __ =>
      elh
        .embed(implicit lh => new Tracing[D] attach new Live(new AddressSql()).mapK(LiftConnectionIO[D].liftF))
        .mapK(txr.trans)
    }

  final private class Live(sql: AddressSql) extends AddressRepository[ConnectionIO] {

    def getAll(pools: List[PoolId], from: Long, to: Long): ConnectionIO[List[Address]] =
      sql.getAll(pools, from, to).to[List]
  }

  final private class Tracing[F[_]: FlatMap: Logging] extends AddressRepository[Mid[F, *]] {

    def getAll(pools: List[PoolId], from: Long, to: Long): Mid[F, List[Address]] =
      for {
        _ <- info"Going to get all addresses from using pools $pools from $from to $to."
        r <- _
        _ <- info"Got all addresses $r."
      } yield r
  }
}

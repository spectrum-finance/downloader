package fi.spectrum
package repositories

import models.AddressState
import repositories.sql.StateSql

import cats.{FlatMap, Functor}
import derevo.derive
import doobie.ConnectionIO
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr
import tofu.higherKind.derived.representableK
import tofu.logging.Logs
import tofu.syntax.monadic._
import cats.tagless.syntax.functorK._

@derive(representableK)
trait StateRepository[F[_]] {
  def insertState(state: AddressState): F[Unit]
}

object StateRepository {

  def create[I[_]: Functor, D[_]: FlatMap: LiftConnectionIO, F[_]](txr: Txr[F, D])(implicit
    elh: EmbeddableLogHandler[D],
    logs: Logs[I, D]
  ): I[StateRepository[F]] =
    logs.forService[StateRepository[F]].map { implicit __ =>
      elh
        .embed(implicit lh => new Live(new StateSql()).mapK(LiftConnectionIO[D].liftF))
        .mapK(txr.trans)
    }

  final private class Live(sql: StateSql) extends StateRepository[ConnectionIO] {

    def insertState(state: AddressState): ConnectionIO[Unit] =
      sql.insert(state).void
  }
}

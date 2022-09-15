package fi.spectrum
package repositories

import models.AddressState

import cats.{Applicative, Functor}
import tofu.logging.Logs
import tofu.syntax.monadic._
import tofu.syntax.logging._

trait StateRepository[F[_]] {
  def insertState(state: AddressState): F[Unit]
}

object StateRepository {
  def create[I[_]: Functor, F[_]: Applicative](implicit logs: Logs[I, F]): I[StateRepository[F]] =
    logs.forService[StateRepository[F]].map(implicit __ =>
      (state: AddressState) => info"Dummy for insert state $state."
    )
}
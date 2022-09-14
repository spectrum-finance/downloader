package fi.spectrum
package processes

import repositories.AddressRepository
import services.WeightResolver

import cats.{Functor, Monad}
import tofu.logging.{Logging, Logs}
import tofu.streams.Evals
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.evals._

trait DownloaderProgram[S[_]] {
  def run: S[Unit]
}

object DownloaderProgram {

  def make[I[_]: Functor, F[_]: Monad, S[_]: Monad: Evals[*[_], F]](implicit
    logs: Logs[I, F],
    addressRepository: AddressRepository[S],
    weightResolver: WeightResolver[F]
  ): I[DownloaderProgram[S]] =
    logs.forService[DownloaderProgram[S]].map(implicit __ => new Live[S, F])

  final private class Live[S[_]: Monad: Evals[*[_], F], F[_]: Monad: Logging](implicit
    addressRepository: AddressRepository[S],
    weightResolver: WeightResolver[F]
  ) extends DownloaderProgram[S] {

    def run: S[Unit] = addressRepository.getAll
      .flatMap { address =>
        eval(info"Start processing next address: $address") >>
        eval(weightResolver.resolve(address))
      }

  }
}

package fi.spectrum
package processes

import configs.ResolverConfig
import repositories.AddressRepository
import services.WeightResolver

import cats.{Functor, Monad}
import tofu.logging.{Logging, Logs}
import tofu.streams.{Emits, Evals}
import tofu.syntax.logging._
import tofu.syntax.monadic._
import tofu.syntax.streams.emits._
import tofu.syntax.streams.evals._

trait DownloaderProgram[S[_]] {
  def run: S[Unit]
}

object DownloaderProgram {

  def make[I[_]: Functor, F[_]: Monad, S[_]: Monad: Evals[*[_], F]: Emits](config: ResolverConfig)(implicit
    logs: Logs[I, F],
    addressRepository: AddressRepository[F],
    weightResolver: WeightResolver[F]
  ): I[DownloaderProgram[S]] =
    logs.forService[DownloaderProgram[S]].map(implicit __ => new Live[S, F](config))

  final private class Live[S[_]: Monad: Evals[*[_], F]: Emits, F[_]: Monad: Logging](config: ResolverConfig)(implicit
    addressRepository: AddressRepository[F],
    weightResolver: WeightResolver[F]
  ) extends DownloaderProgram[S] {

    def run: S[Unit] =
      for {
        addresses <- eval(addressRepository.getAll(config.poolIds, config.from, config.to))
        address   <- emits(addresses)
        _         <- eval(info"Start processing next address: $address")
        _         <- eval(weightResolver.resolve(address))
      } yield ()

  }
}

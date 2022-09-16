package fi.spectrum
package services

import configs.ResolverConfig
import models.Order._
import models.{Address, AddressState, Order, TokenId}
import repositories._

import cats.syntax.traverse._
import cats.{Functor, Monad}
import tofu.logging.{Logging, Logs}
import tofu.syntax.logging._
import tofu.syntax.monadic._

trait WeightResolver[F[_]] {
  def resolve(address: Address): F[Unit]
}

object WeightResolver {

  def make[I[_]: Functor, F[_]: Monad](config: ResolverConfig)(implicit
    logs: Logs[I, F],
    orderRepository: OrderRepository[F],
    stateRepository: StateRepository[F]
  ): I[WeightResolver[F]] =
    logs.forService[WeightResolver[F]].map(implicit __ => new Live[F](config))

  final private class Live[
    F[_]: Monad: Logging
  ](config: ResolverConfig)(implicit
    orderRepository: OrderRepository[F],
    stateRepository: StateRepository[F]
  ) extends WeightResolver[F] {

    def resolve(address: Address): F[Unit] =
      config.poolIds
        .map { poolId =>
          def loopOrder(from: Long, to: Long, state: AddressState): F[Unit] =
            orderRepository.getByAddress(address, poolId, from, to).flatMap { orders =>
              loop(state, orders).flatMap { newState =>
                if (to >= config.to)
                  info"[$address, $poolId]: Processing finished. To is $to, from is: ${config.to}." >> unit
                else
                  info"[$address, $poolId]: start next iteration. From: $to, to: ${to + config.step.toMillis}" >> loopOrder(
                    to,
                    to + config.step.toMillis,
                    newState
                  )
              }
            }

          def loop(state: AddressState, orders: List[Order]): F[AddressState] =
            orders match {
              case order :: tail =>
                val interval = order.timestamp - state.timestamp
                val newLpBalance = order match {
                  case redeem: Redeem   => state.lpBalance - redeem.lp
                  case deposit: Deposit => state.lpBalance + deposit.lp
                }
                val weight   = newLpBalance * interval
                val newState = state.update(newLpBalance, weight, order.timestamp)
                info"[$address, $poolId]: interval: $interval, state: $state, operation: $order, new balance: $newLpBalance, new weight: $weight." >>
                stateRepository.insertState(newState) >> loop(newState, tail)
              case Nil => info"[$address, $poolId]: zero orders left." >> state.pure
            }

          val initialState = AddressState.initial(address, poolId)

          loopOrder(config.from, config.from + config.step.toMillis, initialState)
        }
        .sequence
        .void
  }

}

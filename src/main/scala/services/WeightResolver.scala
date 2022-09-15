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

  val ErgoToken: TokenId = TokenId("0000000000000000000000000000000000000000000000000000000000000000")

  def make[I[_]: Functor, F[_]: Monad](config: ResolverConfig)(implicit
    logs: Logs[I, F],
    orderRepository: OrderRepository[F],
    stateRepository: StateRepository[F],
    poolsRepository: PoolsRepository[F]
  ): I[WeightResolver[F]] =
    logs.forService[WeightResolver[F]].map(implicit __ => new Live[F](config))

  final private class Live[
    F[_]: Monad: Logging
  ](config: ResolverConfig)(implicit
    orderRepository: OrderRepository[F],
    stateRepository: StateRepository[F],
    poolsRepository: PoolsRepository[F]
  ) extends WeightResolver[F] {

    def resolve(address: Address): F[Unit] =
      config.poolIds
        .map { poolId =>
          poolsRepository
            .getPoolInfo(poolId)
            .flatMap { tokenX =>
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
                    val newErgBalance = order match {
                      case redeem: Redeem =>
                        if (tokenX == ErgoToken) state.ergBalance - redeem.outputAmountX
                        else state.ergBalance - redeem.outputAmountY
                      case deposit: Deposit =>
                        if (tokenX == ErgoToken) state.ergBalance + deposit.inputAmountX
                        else state.ergBalance + deposit.inputAmountY
                    }
                    val weight   = newErgBalance * interval
                    val newState = state.update(newErgBalance, weight, order.timestamp)
                    info"[$address, $poolId]: interval: $interval, state: $state, operation: $order, new balance: $newErgBalance, new weight: $weight." >>
                    stateRepository.insertState(newState) >> loop(newState, tail)
                  case Nil => info"[$address, $poolId]: zero orders left." >> state.pure
                }

              val initialState = AddressState.initial(address, poolId)

              loopOrder(config.from, config.from + config.step.toMillis, initialState)
            }
        }
        .sequence
        .void
  }

}

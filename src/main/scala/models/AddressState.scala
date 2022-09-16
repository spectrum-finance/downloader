package fi.spectrum
package models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import doobie.util.Put
import tofu.logging.derivation.loggable

@derive(encoder, decoder, loggable)
final case class AddressState(
  address: Address,
  poolId: PoolId,
  timestamp: Long,
  lpBalance: BigDecimal,
  weight: BigDecimal
) {

  def update(
    newLpBalance: BigDecimal,
    newWeight: BigDecimal,
    newTimestamp: Long
  ): AddressState = this.copy(
    lpBalance = newLpBalance,
    weight    = newWeight,
    timestamp = newTimestamp
  )
}

object AddressState {

  def initial(address: Address, poolId: PoolId): AddressState =
    AddressState(
      address,
      poolId,
      0L,
      BigDecimal(0),
      BigDecimal(0)
    )
}

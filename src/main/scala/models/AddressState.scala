package fi.spectrum
package models

import derevo.derive

final case class AddressState(
  address: Address,
  poolId: PoolId,
  timestamp: Long,
  ergBalance: BigDecimal,
  weight: BigDecimal
) {

  def update(
    newErgBalance: BigDecimal,
    newWeight: BigDecimal,
    newTimestamp: Long
  ): AddressState = this.copy(
    ergBalance = newErgBalance,
    weight     = newWeight,
    timestamp  = newTimestamp
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
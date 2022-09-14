package fi.spectrum
package repositories

import models.AddressState

trait StateRepository[F[_]] {
  def insertState(state: AddressState): F[Unit]
}

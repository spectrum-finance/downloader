package fi.spectrum
package repositories

import models.Address

trait AddressRepository[S[_]] {
  def getAll: S[Address]
}

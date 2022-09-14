package fi.spectrum

import io.estatico.newtype.macros.newtype

package object models {

  @newtype final case class Address(value: String)
  @newtype final case class OrderId(value: String)
  @newtype final case class TokenId(value: String)
  @newtype final case class PoolId(value: String)
}

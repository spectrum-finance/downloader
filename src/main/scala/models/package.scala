package fi.spectrum

import derevo.derive
import derevo.pureconfig.pureconfigReader
import doobie._
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import tofu.logging.Loggable

package object models {

  @newtype final case class Address(value: String)

  object Address {
    implicit val get: Get[Address]          = deriving
    implicit val put: Put[Address]          = deriving
    implicit val logging: Loggable[Address] = deriving
    implicit val encoder: Encoder[Address]  = deriving
    implicit val decoder: Decoder[Address]  = deriving
  }

  @newtype final case class OrderId(value: String)

  object OrderId {
    implicit val get: Get[OrderId]          = deriving
    implicit val put: Put[OrderId]          = deriving
    implicit val logging: Loggable[OrderId] = deriving
    implicit val encoder: Encoder[OrderId]  = deriving
    implicit val decoder: Decoder[OrderId]  = deriving
  }
  @newtype final case class TokenId(value: String)

  object TokenId {
    implicit val get: Get[TokenId]          = deriving
    implicit val put: Put[TokenId]          = deriving
    implicit val logging: Loggable[TokenId] = deriving
    implicit val encoder: Encoder[TokenId]  = deriving
    implicit val decoder: Decoder[TokenId]  = deriving
  }

  @derive(pureconfigReader)
  @newtype final case class PoolId(value: String)

  object PoolId {
    implicit val get: Get[PoolId]          = deriving
    implicit val put: Put[PoolId]          = deriving
    implicit val logging: Loggable[PoolId] = deriving
    implicit val encoder: Encoder[PoolId]  = deriving
    implicit val decoder: Decoder[PoolId]  = deriving
  }
}

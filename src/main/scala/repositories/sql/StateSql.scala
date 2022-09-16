package fi.spectrum
package repositories.sql

import models.AddressState

import doobie.ConnectionIO
import doobie.util.log.LogHandler
import doobie.util.update.Update

final class StateSql(implicit lh: LogHandler) {

  val fields: List[String] = List(
    "address",
    "pool_id",
    "timestamp",
    "lp_balance",
    "weight"
  )

  def insert(state: AddressState): ConnectionIO[Int] =
    Update[AddressState](
      s"insert into state (${fields.mkString(", ")}) values (${fields.map(_ => "?").mkString(", ")})"
    )
      .run(state)
}

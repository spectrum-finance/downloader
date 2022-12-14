package fi.spectrum
package configs

import models.PoolId

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.WithContext
import tofu.logging.derivation.loggable

import scala.concurrent.duration.FiniteDuration

@derive(loggable, pureconfigReader)
final case class ResolverConfig(from: Long, to: Long, step: FiniteDuration, poolIds: List[PoolId])

object ResolverConfig extends WithContext.Companion[ResolverConfig]

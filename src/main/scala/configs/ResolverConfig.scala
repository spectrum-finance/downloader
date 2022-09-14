package fi.spectrum
package configs

import fi.spectrum.models.PoolId
import tofu.{Context, WithContext}

import scala.concurrent.duration.FiniteDuration

final case class ResolverConfig(from: FiniteDuration, to: FiniteDuration, step: FiniteDuration, poolIds: List[PoolId])

object ResolverConfig extends WithContext.Companion[ResolverConfig]

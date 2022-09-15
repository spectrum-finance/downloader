package fi.spectrum
package configs

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.WithContext
import tofu.logging.derivation.loggable
import tofu.optics.macros.{promote, ClassyOptics}

@ClassyOptics
@derive(loggable, pureconfigReader)
final case class ConfigBundle(
  @promote analyticsPg: PgConfig,
  @promote downloaderPg: PgConfig,
  @promote resolver: ResolverConfig
)

object ConfigBundle extends WithContext.Companion[ConfigBundle] with ConfigBundleCompanion[ConfigBundle]

package fi.spectrum
package configs

import derevo.derive
import tofu.WithContext
import tofu.logging.derivation.loggable
import tofu.optics.macros.{promote, ClassyOptics}

@ClassyOptics
@derive(loggable)
final case class AppContext(
  @promote config: ConfigBundle
)

object AppContext extends WithContext.Companion[AppContext] {

  def init(configs: ConfigBundle): AppContext =
    AppContext(configs)
}

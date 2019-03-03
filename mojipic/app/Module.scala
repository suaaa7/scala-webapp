import java.time.Clock

import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}

class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {

  def configure() = {
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
  }
}

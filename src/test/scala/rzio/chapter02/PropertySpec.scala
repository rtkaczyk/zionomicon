package rzio.chapter02

import zio._
import zio.test._
import Assertion._
import zio.test.environment.Live

object PropertySpec extends DefaultRunnableSpec:
  def spec = suite("PropertySpec")(
    testM("java.io.tmpdir") {
      val propKey = "java.io.tmpdir"

      for
        jProp <- ZIO.effect(System.getProperty(propKey))
        zProp <- system
          .property(propKey)
          .provideLayer(environment.liveEnvironment)
          .someOrElse("fail!")
        yProp <- Live.live(system.property(propKey)).someOrElse("also fail!")
      yield assert(zProp)(equalTo(jProp) && equalTo(yProp))
    }
  )

package rzio.chapter02

import zio._
import zio.console._
import rzio.chapter02.E01_Files._

// Ex 10
object E10_Cat extends App:
  def run(args: List[String]) =
    ZIO
      .foreach(args.map(Path(_))) { p =>
        readFileZio(p).flatMap(c => putStrLn(c.underlying))
      }
      .exitCode

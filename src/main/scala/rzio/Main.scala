package rzio

import zio._
import zio.console._

object Main extends App:
  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    putStrLn("Zionomicon").exitCode

  import quoted.*

  def isCaseClass[T: Type](using Quotes): Boolean = {
    import quoted.quotes.reflect.*
    val symbol = TypeRepr.of[T].typeSymbol
    symbol.caseFields.nonEmpty
  }

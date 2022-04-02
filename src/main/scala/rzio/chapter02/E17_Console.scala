package rzio.chapter02

import zio._
import zio.console._
import zio.random._
import zio.{App => ZIOApp}
import zio.console.Console
import java.io.IOException

// Ex 17-20
object E17_Console:

  trait HelloHuman extends ZIOApp:
    def run(args: List[String]) =
      val logic =
        for
          _    <- putStrLn("Wat name?: ")
          name <- getStrLn
          _    <- putStrLn(s"Herro $name")
        yield ()
      logic.exitCode

  trait NumberGuessing extends ZIOApp:
    def run(args: List[String]) =
      val read  = putStr("Guess (1-3): ") *> getStrLn.map(_.toInt).option
      val makeR = nextIntBetween(1, 4)
      val guess = (makeR <*> read).tap {
        case (r, g) if g.contains(r) => putStrLn("Correct!")
        case (r, _)                  => putStrLn(s"Wrong! It was $r")
      }
      doWhile(guess)(x => x(1).contains(x(0))).exitCode

  def readUntil(acceptInput: String => Boolean): ZIO[Has[Console.Service], IOException, String] =
    doWhile(getStrLn)(acceptInput)

  def doWhile[R, E, A](body: ZIO[R, E, A])(condition: A => Boolean): ZIO[R, E, A] =
    body.filterOrElse_(condition)(doWhile(body)(condition))

object NumberGuessing extends E17_Console.NumberGuessing
object HelloHuman     extends E17_Console.HelloHuman

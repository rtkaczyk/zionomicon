package rzio.chapter02

import zio._

// Ex 4-5
object E04_Rewrites:
  object Task4:
    def printLine(line: String) = ZIO.effect(println(line))
    val readLine                = ZIO.effect(scala.io.StdIn.readLine())

    def withFlatMap = printLine("What is your name?")
      .flatMap(_ => readLine.flatMap(name => printLine(s"Hello, ${name}!")))

    def withFor =
      for
        _    <- printLine("What is your name?")
        name <- readLine
        _    <- printLine(s"Hello, ${name}!")
      yield ()

  object Task5:
    val random                  = ZIO.effect(scala.util.Random.nextInt(3) + 1)
    def printLine(line: String) = ZIO.effect(println(line))
    val readLine                = ZIO.effect(scala.io.StdIn.readLine())

    def withFlatMap = random.flatMap { int =>
      printLine("Guess a number from 1 to 3:").flatMap { _ =>
        readLine.flatMap { num =>
          if (num == int.toString)
            printLine("You guessed right!")
          else
            printLine(s"You guessed wrong, the number was $int!")
        }
      }
    }

    def withFor =
      for
        int <- random
        _   <- printLine("Guess a number from 1 to 3:")
        num <- readLine
        _ <-
          if num == int.toString then
            printLine("You guessed right!")
          else
            printLine(s"You guessed wrong, the number was $int!")
      yield ()

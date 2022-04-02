package rzio.chapter04

import zio._
import zio.test._
import zio.console._
import zio.test.Assertion._
import zio.test.environment.TestConsole
import zio.test.Assertion.Render.param
import java.io.IOException

object ExercisesSpec extends DefaultRunnableSpec:
  import Exercises._

  def spec = suite("Chapter 04 exercises")(
    testM("failWithMessage") {
      assertM(failWithMessage("fail!").run)(fails(isSubtype[Error](anything)))
    },
    testM("recoverFromSomeDefects") {
      val success   = ZIO.succeed("success")
      val error     = ZIO.fail("error")
      val recovered = ZIO.succeed(throw new Exception("recoverable"))
      val defect    = ZIO.succeed(throw new Exception("defect"))

      val res = ZIO.collectAll(
        List(success, error, recovered, defect).map(
          recoverFromSomeDefects(_) { t =>
            t.getMessage match {
              case "recoverable" => Some("recovered")
              case _             => None
            }
          }.run
        )
      )

      assertM(res)(
        hasAt(0)(succeeds(equalTo("success"))) &&
          hasAt(1)(fails(equalTo("error"))) &&
          hasAt(2)(succeeds(equalTo("recovered"))) &&
          hasAt(3)(dies(isSubtype[Exception](anything)))
      )
    },
    testM("logFailures") {
      val success = ZIO.succeed("success")
      val error   = ZIO.fail("error")
      val defect  = ZIO.succeed(throw new Exception("defect"))

      val zios = List(success, error, defect).map(logFailures(_).run)
      def outputContains(ss: String*): Assertion[Vector[String]] =
        assertion("outputContains")(param(ss.mkString(","))) { v =>
          ss.forall(s => v.exists(_.contains(s)))
        }

      for
        res <- TestConsole.silent(ZIO.collectAll(zios))
        out <- TestConsole.output
      yield assert(out)(outputContains("error", "defect"))
    },
    testM("onAnyFailure") {
      def sideEff(s: String) = TestConsole.silent(putStr(s))

      val success = onAnyFailure(ZIO.succeed("success"), sideEff("success"))
      val error   = onAnyFailure(ZIO.fail("error"), sideEff("error"))
      val defect  = onAnyFailure(ZIO.succeed(throw new Exception("defect")), sideEff("defect"))

      for
        _    <- TestConsole.clearOutput
        s    <- success.run
        out1 <- TestConsole.output

        _    <- TestConsole.clearOutput
        e    <- error.run
        out2 <- TestConsole.output

        _    <- TestConsole.clearOutput
        d    <- defect.run
        out3 <- TestConsole.output
      yield
        assert(s)(succeeds(anything)) &&
          assert(e)(fails(anything)) &&
          assert(d)(dies(anything)) &&
          assert(out1)(isEmpty) &&
          assert(out2.head)(equalTo("error")) &&
          assert(out3.head)(equalTo("defect"))
    },
    testM("ioException") {
      val error  = ZIO.fail(new IOException("error"))
      val defect = ZIO.fail(new Exception("defect"))

      for
        e <- ioException(error).run
        d <- ioException(defect).run
      yield assert(e)(fails(anything)) && assert(d)(dies(anything))
    },
    testM("left/unleft") {
      case object Oops
      case object Fail

      val success = ZIO.succeed(Left("success"))
      val fail    = ZIO.succeed(Right(Fail))
      val oops    = ZIO.fail(Oops)

      for
        ls <- left(success)
        lf <- left(fail).run
        lo <- left(oops).run
        us <- unleft(left(success))
        uf <- unleft(left(fail))
        uo <- unleft(left(oops)).run
      yield
        assert(ls)(equalTo("success")) &&
          assert(lf)(fails(equalTo(Right(Fail)))) &&
          assert(lo)(fails(equalTo(Left(Oops)))) &&
          assert(us)(equalTo(Left("success"))) &&
          assert(uf)(equalTo(Right(Fail))) &&
          assert(uo)(fails(equalTo(Oops)))
    },
    testM("right/unright") {
      case object Oops
      case object Fail

      val success = ZIO.succeed(Right("success"))
      val fail    = ZIO.succeed(Left(Fail))
      val oops    = ZIO.fail(Oops)

      for
        rs <- right(success)
        rf <- right(fail).run
        ro <- right(oops).run
        us <- unright(right(success))
        uf <- unright(right(fail))
        uo <- unright(right(oops)).run
      yield
        assert(rs)(equalTo("success")) &&
          assert(rf)(fails(equalTo(Right(Fail)))) &&
          assert(ro)(fails(equalTo(Left(Oops)))) &&
          assert(us)(equalTo(Right("success"))) &&
          assert(uf)(equalTo(Left(Fail))) &&
          assert(uo)(fails(equalTo(Oops)))
    }
  )

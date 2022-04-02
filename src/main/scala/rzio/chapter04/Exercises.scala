package rzio.chapter04

import zio._
import zio.console._
import java.io.IOException

object Exercises:
  // 01
  def failWithMessage(string: String): Task[Nothing] =
    ZIO.effect(throw new Error(string))

  // 02
  def recoverFromSomeDefects[R, E, A](zio: ZIO[R, E, A])(
      f: Throwable => Option[A]
  ): ZIO[R, E, A] =
    zio.foldCauseM(
      failure = { cause =>
        val defect = cause.defects.view.map(f).find(_.isDefined).flatten
        defect.fold(zio)(ZIO.succeed)
      },
      success = _ => zio
    )

  // 03
  def logFailures[R, E, A](zio: ZIO[R, E, A])(using R <:< Console): ZIO[R & Console, E, A] =
    zio.foldCauseM(
      failure = { c => putStrLn(c.prettyPrint).orElse(ZIO.unit) *> zio },
      success = _ => zio
    )

  // 04
  def onAnyFailure[R, E, A](
      zio: ZIO[R, E, A],
      handler: ZIO[R, E, Any]
  ): ZIO[R, E, A] =
    zio.foldCauseM(
      failure = _ => handler *> zio,
      success = _ => zio
    )

  // 05
  def ioException[R, A](
      zio: ZIO[R, Throwable, A]
  ): ZIO[R, java.io.IOException, A] =
    zio.refineOrDie {
      case ex: IOException => ex
    }

  // 06
  val parseNumber: ZIO[Any, NumberFormatException, Int] =
    ZIO.effect("foo".toInt).refineOrDie {
      case ex: NumberFormatException => ex
    }

  // 07
  def left[R, E, A, B](
      zio: ZIO[R, E, Either[A, B]]
  ): ZIO[R, Either[E, B], A] =
    zio.foldM(
      success = _.fold(ZIO.succeed, b => ZIO.fail(Right(b))),
      failure = e => ZIO.fail(Left(e))
    )

  def unleft[R, E, A, B](
      zio: ZIO[R, Either[E, B], A]
  ): ZIO[R, E, Either[A, B]] =
    zio.foldM(
      success = a => ZIO.succeed(Left(a)),
      failure = _.fold(e => ZIO.fail(e), b => ZIO.succeed(Right(b)))
    )

  // 08
  def right[R, E, A, B](
      zio: ZIO[R, E, Either[A, B]]
  ): ZIO[R, Either[E, A], B] =
    zio.foldM(
      success = _.fold(a => ZIO.fail(Right(a)), ZIO.succeed),
      failure = e => ZIO.fail(Left(e))
    )

  def unright[R, E, A, B](
      zio: ZIO[R, Either[E, A], B]
  ): ZIO[R, E, Either[A, B]] =
    zio.foldM(
      success = b => ZIO.succeed(Right(b)),
      failure = _.fold(e => ZIO.fail(e), a => ZIO.succeed(Left(a)))
    )

  // 09
  def catchAllCause[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    zio.sandbox.foldM(handler, ZIO.succeed)

  // 10
  def catchAllCause2[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    zio.foldCauseM(handler, ZIO.succeed)

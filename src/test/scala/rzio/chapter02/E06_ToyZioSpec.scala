package rzio.chapter02

import cats.syntax.all._
import zio.test.*
import zio.test.Assertion._
import E06_ToyZio.*

import scala.util.Try

object E06_ToyZioSpec extends DefaultRunnableSpec:
  type Z[A] = ZIO[String, Error, A]
  def Z[A](a: String => Either[Error, A]): Z[A] = ZIO[String, Error, A](a)

  enum Error:
    case First, Second, ToInt, NA

  import Error._

  val concat: Z[String] = Z(s => Right(s + s))
  val first: Z[String]  = Z(s => Try(s.head.toString).toEither.left.map(_ => First))
  val second: Z[String] = Z(s => Try(s.tail.head.toString).toEither.left.map(_ => Second))
  val toInt: Z[Int]     = Z(s => Try(s.toInt).toEither.left.map(_ => ToInt))

  def spec =
    suite("ToyZio")(
      testM("zipWith") {
        val cases = List(
          (zipWith(concat, toInt)(_ + _), "10", "101010".asRight),
          (zipWith(concat, toInt)(_ + _), "abc", ToInt.asLeft),
          (zipWith(first, toInt)(_ + _), "", First.asLeft),
          (zipWith(zipWith(first, second)(_ + _), concat)(_ + _), "ab", "ababab".asRight)
        )

        checkAll(Gen.fromIterable(cases)) { case (z, s, r) =>
          assert(z.run(s))(equalTo(r))
        }
      },
      testM("collectAll") {
        val cases = List(
          (collectAll(List(concat, first, second, second)), "ab", List("abab", "a", "b", "b").asRight),
          (collectAll(List(first, second)), "a", Second.asLeft),
          (collectAll(List(first, second)), "", First.asLeft),
          (collectAll(Nil), "whatever", Nil.asRight)
        )

        checkAll(Gen.fromIterable(cases)) { case (z, s, r) =>
          assert(z.run(s))(equalTo(r))
        }
      },
      testM("foreach") {
        val s2z: Char => Z[String] = {
          case 'a' =>
            concat
          case 'b' =>
            first
          case 'c' =>
            second
          case _ =>
            ZIO(_ => NA.asLeft)
        }

        val cases = List(
          ("abc".toList, "ab", List("abab", "a", "b").asRight),
          ("abc".toList, "a", Second.asLeft),
          ("abd".toList, "whatever", NA.asLeft)
        )

        checkAll(Gen.fromIterable(cases)) { case (in, s, r) =>
          val r_ = foreach(in)(s2z).run(s)
          assert(r_)(equalTo(r))
        }
      },
      testM("orElse") {
        val z     = orElse(second, first)
        val cases = List(("", First.asLeft), ("a", "a".asRight), ("ab", "b".asRight))

        checkAll(Gen.fromIterable(cases)) { case (s, r) =>
          assert(z.run(s))(equalTo(r))
        }
      }
    )

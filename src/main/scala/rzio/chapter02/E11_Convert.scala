package rzio.chapter02

import zio._

//Ex 11-12
object E11_Convert:
  def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] =
    either.fold(ZIO.fail, ZIO.succeed)

  def listToZIO[A](list: List[A]): ZIO[Any, None.type, A] =
    list.headOption.fold(ZIO.fail(None))(ZIO.succeed)

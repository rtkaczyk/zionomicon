package rzio.chapter02

import zio._
import scala.concurrent.{ExecutionContext, Future}

// Ex 16
object E16_Future:
  trait Query
  trait Result

  def doQuery(query: Query)(implicit ec: ExecutionContext): Future[Result] = ???

  def doQueryZio(query: Query): ZIO[Any, Throwable, Result] =
    ZIO.fromFuture(doQuery(query))

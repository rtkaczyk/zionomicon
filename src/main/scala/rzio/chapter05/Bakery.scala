package rzio.chapter05

import zio.*
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.request._
import org.http4s.dsl.impl.Responses._
import org.http4s.blaze.server._
import zio.interop.catz._
import zio.interop.catz.implicits._

object Bakery extends App:
  val dsl = Http4sDsl[Task]
  import dsl._

  val helloWorldService: HttpApp[Task] = HttpRoutes.of[Task] {
    case GET -> Root / "hello" / name =>
      Ok(name.toString.toUpperCase)
//      Task.succeed(
//        Response[Task](Status.Ok).withEntity(name.toString.toUpperCase)
//      )
  }.orNotFound

  def run(args: List[String]): URIO[ZEnv, zio.ExitCode] =
    BlazeServerBuilder[Task](asyncRuntimeInstance)
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .exitCode

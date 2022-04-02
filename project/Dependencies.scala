import sbt._

object Dependencies {
  val zioV    = "1.0.12"
  val http4sV = "0.23.6"
  val doobieV = "1.0.0-RC1"

  val dependencies = List(
    "org.typelevel" %% "cats-core"           % "2.6.1",
    "dev.zio"       %% "zio"                 % zioV,
    "dev.zio"       %% "zio-streams"         % zioV,
    "dev.zio"       %% "zio-test"            % zioV      % Test,
    "dev.zio"       %% "zio-test-sbt"        % zioV      % Test,
    "dev.zio"       %% "zio-interop-cats"    % "3.1.1.0",
    "org.http4s"    %% "http4s-dsl"          % http4sV,
    "org.http4s"    %% "http4s-blaze-server" % http4sV,
    "org.http4s"    %% "http4s-blaze-client" % http4sV,
    "org.tpolecat"  %% "doobie-core"         % doobieV,
    "org.tpolecat"  %% "doobie-h2"           % doobieV,
    "com.h2database" % "h2"                  % "1.4.200" % Test
  )
}

import Dependencies.dependencies

val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "zionomicon",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= dependencies,
    scalacOptions ++= List("-Yindent-colons"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

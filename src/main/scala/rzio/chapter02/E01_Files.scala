package rzio.chapter02

import zio.*

import java.nio.file.{Files => nioFiles}
import java.nio.file.Paths
import scala.annotation.targetName

//Ex 1-3
object E01_Files:
  private def readFile(file: String): String =
    val source = scala.io.Source.fromFile(file)
    try source.getLines.mkString("\n")
    finally source.close()

  private def writeFile(file: String, text: String): Unit =
    import java.io._
    val pw = new PrintWriter(new File(file))
    try pw.write(text)
    finally pw.close

  opaque type Path = String
  object Path:
    def apply(s: String): Path = s
    val sep                    = "/"
  extension (p: Path)
    @targetName("pathUnderlying")
    def underlying: String = p
    def /(child: String): Path = p + Path.sep + child

  opaque type Contents = String
  object Contents:
    def apply(s: String): Contents = s
  extension (c: Contents)
    @targetName("contentsUnderlying")
    def underlying: String = c

  def readFileZio(file: Path): Task[Contents] =
    ZIO.effect(readFile(file))

  def writeFileZio(file: Path, text: Contents): Task[Unit] =
    ZIO.effect(writeFile(file, text))

  def deleteFileZio(file: Path): Task[Unit] =
    ZIO.effect(nioFiles.delete(Paths.get(file)))

  def fileExistsZio(file: Path): Task[Boolean] =
    ZIO.effect(nioFiles.exists(Paths.get(file)))

  def copyFileZio(source: Path, dest: Path): Task[Unit] =
    for
      read <- readFileZio(source)
      _    <- writeFileZio(dest, read)
    yield ()

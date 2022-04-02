package rzio.chapter02

import zio.*
import zio.test.*
import zio.test.Assertion._
import zio.test.TestAspect.*
import E01_Files.*

object E01_FilesSpec extends DefaultRunnableSpec:
  def spec =
    suite("Files Spec")(
      testM("read/writeFileZio") {
        for
          filePath <- filePath("zionomicon.tmp")
          contents = Contents("some dummy text")
          _      <- writeFileZio(filePath, contents)
          read   <- readFileZio(filePath)
          _      <- deleteFileZio(filePath)
          exists <- fileExistsZio(filePath)
        yield assert(read)(equalTo(contents)) && assertTrue(!exists)
      },
      testM("copyFileZio") {
        for
          source <- filePath("source.tmp")
          dest   <- filePath("dest.tmp")
          contents = Contents("some dummy text")
          _    <- writeFileZio(source, contents)
          _    <- copyFileZio(source, dest)
          read <- readFileZio(dest)
          _    <- deleteFileZio(source)
          _    <- deleteFileZio(dest)
        yield assert(read)(equalTo(contents))
      }
    )

  def filePath(name: String): RIO[environment.Live, Path] = environment
    .live(system.property("java.io.tmpdir"))
    .someOrFailException
    .map(p => Path(p) / name)

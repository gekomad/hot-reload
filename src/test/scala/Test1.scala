import org.scalatest.FunSuite

import scala.util.{Failure, Success, Try}

class Test1 extends FunSuite {

  val tmpDir: String = System.getProperty("java.io.tmpdir")

  def copyFile(from: String, to: String): Unit = {
    import java.nio.file.Files.copy
    import java.nio.file.Paths.get
    import java.nio.file.StandardCopyOption.REPLACE_EXISTING

    implicit def toPath(filename: String) = get(filename)

    Try(copy(from, to, REPLACE_EXISTING)) match {
      case Success(_) =>
      case Failure(f) => println(f)
    }
  }

  case class FooConfig(bar: String, baz: Option[Int], list: List[Int], missingValue: Option[String])

  /** conf1.conf
      {
        bar = "conf_1",
        baz = 42,
        list = [1, 2, 3]
      }
    */
  val confPath1 = getClass.getResource("conf1.conf").getPath
  val conf1     = FooConfig("conf_1", Some(1), List(1, 11, 111), None)

  /** conf2.conf
      {
        bar = "conf_2",
        baz = 42,
        list = [1, 2, 3]
      }
    */
  val confPath2 = getClass.getResource("conf2.conf").getPath
  val conf2     = FooConfig("conf_2", Some(2), List(2, 22, 222), None)

  /** confErr.conf
      {
        bar = 1, // bar must be String
        baz = 42,
        list = [1, 2, 3]
      }
    */
  val confPathErr = getClass.getResource("confErr.conf").getPath

  test("Immutable Config") {
    import com.github.gekomad.hotreload.core.HotReload

    val confFile = s"$tmpDir/hot_reload.conf"

    //create HotReload from conf1.conf file
    copyFile(from = confPath1, to = confFile)

    //create HotReload from conf file, set 'mutable = false' to make it immutable
    val hr: Try[HotReload[FooConfig]] = HotReload[FooConfig](confFile, mutable = false)

    hr match {
      case Success(hotReload) =>
        // hotReload contains conf1
        assert(hotReload.currentConf == conf1)

        // modifying conf file, hotReload still contains the first one conf class because it's immutable
        copyFile(from = confPath2, to = confFile)
        Thread.sleep(1000)

        assert(hotReload.currentConf == conf1)

        assert(hotReload.filePath == s"$tmpDir/hot_reload.conf")

      case Failure(f) => println(s"error on loading file ${f.getMessage}")
    }
  }

  test("Mutable Config") {
    import com.github.gekomad.hotreload.core.HotReload
    //create conf file
    val confFile = s"$tmpDir/hot_reload.conf"

    copyFile(from = confPath1, to = confFile)

    //create HotReload from conf1.conf file
    val hr: Try[HotReload[FooConfig]] = HotReload[FooConfig](confFile, mutable = true)

    hr match {
      case Success(hotReload) => {

        {

          /**
            * hotReload contains conf1
            */
          assert(hotReload.currentConf == conf1)

        }

        {

          /**
            * modifying conf file, hotReload contains conf2
            */
          Thread.sleep(1000)
          copyFile(from = confPath2, to = confFile)
          Thread.sleep(1000)
          assert(hotReload.currentConf == conf2)

        }

        {

          /**
            *  modifying again conf file hotReload still contains conf2 because the last one file was wrong
            */
          copyFile(from = confPathErr, to = confFile)
          Thread.sleep(1000)
          assert(hotReload.currentConf == conf2)

        }

        {

          /**
            * filePath returns the file path
            */
          assert(hotReload.filePath == s"$tmpDir/hot_reload.conf")

        }
      }
      case Failure(f) => println(s"error on loading file ${f.getMessage}")
    }
  }

}

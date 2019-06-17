package com.github.gekomad.hotreload.core

import java.nio.file.Paths
import java.util.Date

import com.lambdista.config.{ConcreteValue, Config}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait DynamicConf[A] {

  /**
    *
    * @param filePath       is the file path of conf file
    */
  val filePath: String
  protected var current: A

  /**
    *
    * @return       the current conf class
    */
  def currentConf: A = current

}

/**
  * Creates a HotReload
  *
  * @author Giuseppe Cannella
  * @since 0.1.0
  * @param mutable   if false creates an immutable config
  * @param conf      the conf case class
  * @param filePath  the conf file
  * @see See test code for more information
  * @see See [[https://github.com/gekomad/hot-reload/blob/master/README.md]] for more information
  */
case class HotReload[A: ConcreteValue](mutable: Boolean, conf: A, filePath: String) extends DynamicConf[A] {

  /**
    *
    * @param filePath       is the file path of conf file
    */
  override protected var current: A = conf

  if (mutable) Future {

    import java.io.File
    import scala.collection.JavaConverters._
    val dir = new File(filePath).getParent
    import java.nio.file._
    val watchService: WatchService = FileSystems.getDefault.newWatchService

    val path: Path       = Paths.get(dir)
    val fileName: String = new File(filePath).getName
    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

    while (true) {
      Try(watchService.take) match {
        case Success(key) =>
          key.pollEvents.asScala.toList.filter(_.context.toString == fileName).map { event =>
            val fooConfig: Try[A] = for {
              conf   <- Config.from(Paths.get(filePath))
              result <- conf.as[A]
            } yield result

            fooConfig match {
              case Success(value) =>
                if (value != current) {
                  current = value
                }
                ()
              case Failure(e) =>
                println(s"${new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S").format(new Date)} ERROR ${this.getClass.getName} - error on loading conf file: $filePath msg: $e")
                ()
            }
          }
          key.reset()
        case Failure(f) =>
          System.err.println(s"ERROR on watchService $f")
      }
    }
  }

}

object HotReload {

  /**
    *
    * @param path the path of conf file
    * @param mutable if true the conf class can be modified
    * @return  the HotReload case class
    * {{{
    * import com.github.gekomad.hotreload.core.HotReload
    *
    * val hr: Try[HotReload[FooConfig]] = HotReload[FooConfig](confFile)
    *
    *}}}
    */
  def apply[A: ConcreteValue](path: String, mutable: Boolean): Try[HotReload[A]] = {

    val fooConfig: Try[A] = for {
      conf   <- Config.from(Paths.get(path))
      result <- conf.as[A]
    } yield result

    fooConfig match {
      case Success(value) =>
        Success(HotReload[A](mutable, value, path))
      case Failure(e) => Failure(e)
    }

  }

}

package zio.sql

import com.dimafeng.testcontainers.SingleContainer
import com.dimafeng.testcontainers.MSSQLServerContainer
import org.testcontainers.utility.DockerImageName
import zio._
import zio.blocking.{ effectBlocking, Blocking }

object TestContainer {

  def container[C <: SingleContainer[_]: Tag](c: C): ZLayer[Blocking, Throwable, Has[C]] =
    ZManaged.make {
      effectBlocking {
        c.start()
        c
      }
    }(container => effectBlocking(container.stop()).orDie).toLayer

  def sqlServer(imageName: DockerImageName): ZLayer[Blocking, Throwable, Has[MSSQLServerContainer]] =
    ZManaged.make {
      effectBlocking {
        val c = new MSSQLServerContainer(
          dockerImageName = imageName
        ).configure { a =>
          a.withInitScript("shop_schema.sql")
          a.withEnv("ACCEPT_EULA", "Y")
          ()
        }
        c.start()
        c
      }
    }(container => effectBlocking(container.stop()).orDie).toLayer

}

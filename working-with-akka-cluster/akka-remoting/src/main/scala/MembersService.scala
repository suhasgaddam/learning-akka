import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

object MembersService extends App with LazyLogging {
  val config = ConfigFactory.load.getConfig("members-service")

  val system = ActorSystem("members-service", config)

  val worker = system.actorOf(Props[Worker], "remote-worker")

  logger.info(s"Worker actor path is ${worker.path}")

  Thread.sleep(10000)

  system.terminate()
}

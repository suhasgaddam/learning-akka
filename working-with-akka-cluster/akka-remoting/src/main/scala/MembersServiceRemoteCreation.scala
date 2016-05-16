import Worker.Work
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

object MembersServiceRemoteCreation extends App with LazyLogging {
  val config = ConfigFactory.load.getConfig("members-service-remote-creation")

  val system = ActorSystem("members-service-remote-creation", config)

  val worker = system.actorOf(Props[Worker], "worker-actor-remote")

  logger.info(s"The remote path of worker Actor is ${worker.path}")

  worker ! Work("Hi Remote Actor!")

  Thread.sleep(10000)

  system.terminate()
}

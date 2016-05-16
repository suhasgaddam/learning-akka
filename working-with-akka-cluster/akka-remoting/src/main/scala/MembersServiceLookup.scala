import Worker.Work
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

object MembersServiceLookup extends App with LazyLogging {
  val config = ConfigFactory.load.getConfig("members-service-lookup")

  val system = ActorSystem("members-service-lookup", config)

  val worker = system.actorSelection("akka.tcp://members-service@127.0.0.1:2552/user/remote-worker")

  worker ! Work("Hi Remote Worker!")

  Thread.sleep(10000)

  system.terminate()
}

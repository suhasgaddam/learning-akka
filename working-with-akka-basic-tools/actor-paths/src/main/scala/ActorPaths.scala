import akka.actor.{ActorSystem, PoisonPill, Props}
import com.typesafe.scalalogging.LazyLogging

object ActorPaths extends App with LazyLogging {
  val system = ActorSystem("actor-paths")

  val counter1 = system.actorOf(Props[Counter], "counter")

  val watcher = system.actorOf(Props[Watcher], "watcher")

  val counterSelection1 = system.actorSelection("counter")

  logger.info(s"Actor Reference for counter1: $counter1")
  logger.info(s"Actor Selection for counter1: $counterSelection1")

  counter1 ! PoisonPill

  Thread.sleep(1000)

  val counter2 = system.actorOf(Props[Counter], "counter")

  val counterSelection2 = system.actorSelection("counter")

  logger.info(s"Actor Reference for counter2: $counter2")
  logger.info(s"Actor Selection for counter2: $counterSelection2")

  system.terminate()
}

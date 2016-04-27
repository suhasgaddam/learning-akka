import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy._

import scala.concurrent.duration._

object Aphrodite {
  case object ResumeException extends Exception
  case object StopException extends Exception
  case object RestartException extends Exception

  def props = Props[Aphrodite]
}

class Aphrodite extends Actor with ActorLogging {
  import Aphrodite._

  override def preStart() = log.info("preStart hook")

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    log.info("preRestart hook")

    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    log.info("postRestart hook")

    super.postRestart(reason)
  }

  override def postStop() = log.info("postStop hook")

  override def receive = {
    case "Resume" => throw ResumeException
    case "Stop" => throw StopException
    case "Restart" => throw RestartException
    case _ => throw new Exception
  }
}

class Hera extends Actor with ActorLogging {
  import Aphrodite._

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
    case ResumeException => Resume
    case StopException => Stop
    case RestartException => Restart
    case _: Exception => Escalate
  }

  var aphrodite: ActorRef = _

  override  def preStart() = {
    aphrodite = context.actorOf(Aphrodite.props, "aphrodite")

    Thread.sleep(1000)
  }

  override def receive = {
    case message => {
      log.info(s"Received $message")

      aphrodite ! message

      Thread.sleep(1000)
    }
  }
}

object  Supervision extends App {
  val system = ActorSystem("supervision")

  val hera = system.actorOf(Props[Hera], "hera")

//  hera ! "Resume"
//  hera ! "Restart"
  hera ! "Stop"

  Thread.sleep(1000)

  system.terminate()
}

import akka.actor.{Actor, ActorLogging}

object Worker {
  case class Work(message: String)
}

class Worker extends Actor with ActorLogging {
  import Worker._

  override def receive = {
    case message: Work => log.info(s"I received Work Message($message). My ActorRef is $self")
  }

}

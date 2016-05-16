import akka.actor.{Actor, ActorLogging}

object Worker {
  case class Work()
}

class Worker extends Actor with ActorLogging {
  import Worker._

  override def receive = {
    case message: Work => log.info(s"I received a Work Message. My ActorRef is $self")
  }
}

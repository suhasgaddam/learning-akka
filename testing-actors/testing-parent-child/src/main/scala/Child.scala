import akka.actor.{Actor, ActorLogging, ActorRef}

class Child(parent: ActorRef) extends Actor with ActorLogging {
  override def receive = {
    case Ping => parent ! Pong
  }
}

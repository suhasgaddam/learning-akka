import akka.actor.{Actor, ActorLogging, ActorRef, ActorRefFactory}

case object Ping
case object Pong

class Parent(childMaker: ActorRefFactory => ActorRef) extends Actor with ActorLogging {
  val child = childMaker(context)

  var ponged = false

  override def receive = {
    case Ping => child ! Ping
    case Pong => ponged = true
  }
}

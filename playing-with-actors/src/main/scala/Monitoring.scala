import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}

class Ares(athena: ActorRef) extends Actor with ActorLogging {
  override def preStart() = context.watch(athena)

  override def postStop() = log.info("postStop")

  override def receive = {
    case Terminated => context.stop(self)
  }
}

class Athena extends Actor with ActorLogging {
  override def receive = {
    case message => {
      log.info(s"Received $message")

      context.stop(self)
    }
  }
}

object Monitoring extends App {
  val system = ActorSystem("monitoring")

  val athena = system.actorOf(Props[Athena], "athena")

  val ares = system.actorOf(Props(classOf[Ares], athena), "ares")

  athena ! "Hello"

  system.terminate()
}

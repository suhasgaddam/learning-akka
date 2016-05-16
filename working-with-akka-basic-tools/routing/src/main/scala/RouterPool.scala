import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class RouterPool extends Actor with ActorLogging {
  var routees: List[ActorRef] = _

  override def preStart() = {
    routees = List.fill(5)(context.actorOf(Props[Worker]))
  }

  override def receive = {
    case message: Worker.Work => {
      log.info("I'm a Router Pool. I received a Work Message.")

      routees(util.Random.nextInt(routees.size)) forward message
    }
  }
}

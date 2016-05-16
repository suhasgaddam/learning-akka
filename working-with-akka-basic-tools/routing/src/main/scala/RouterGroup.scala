import akka.actor.{Actor, ActorLogging}

class RouterGroup(routees: List[String]) extends Actor with ActorLogging {
  override def receive = {
    case message: Worker.Work => {
      log.info("I'm a Router Group. I received a Work Message.")

      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward message
    }
  }
}

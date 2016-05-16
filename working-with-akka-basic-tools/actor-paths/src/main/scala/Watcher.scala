import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, Identify}

class Watcher extends Actor with ActorLogging {

  var counterRef: ActorRef = _

  var selection = context.actorSelection("/user/counter")

  selection ! Identify(None)

  override def receive = {
    case ActorIdentity(_, Some(reference)) => log.info(s"Actor reference for counter is $reference")
    case ActorIdentity(_, None) => log.info(s"Actor selection for actor that doesn't exist :(")
  }
}

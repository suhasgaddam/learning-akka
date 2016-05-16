import Counter.{Decrement, GetCount, Increment}
import akka.actor.{Actor, ActorLogging}

object Counter {
  case object Increment
  case object Decrement
  case object GetCount
}

class Counter extends Actor with ActorLogging {

  var count = 0

  override def receive = {
    case Increment => count += 1
    case Decrement => count -= 1
    case GetCount => sender ! count
  }
}

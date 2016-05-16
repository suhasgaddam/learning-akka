import akka.actor.{Actor, ActorLogging}

object Counter {
  case class Increment(amount: Int)
  case class Decrement(amount: Int)
}

class Counter extends Actor with ActorLogging {
  import Counter._

  var count = 0

  override def receive = {
    case Increment(amount) => {
      log.info(s"Increment $amount")

      count += amount
    }
     case Decrement(amount) => {
      log.info(s"Decrement $amount")

      count -= amount
    }
  }
}

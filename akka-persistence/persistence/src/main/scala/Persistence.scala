import Counter.{Command, Increment}
import akka.actor.{ActorSystem, Props}

object Persistence extends App {
  val system = ActorSystem("persistence")

  val counter = system.actorOf(Props[Counter], "counter")

  counter ! Command(Increment(3))

  counter ! Command(Increment(5))

  counter ! Command(Increment(7))

  counter ! "print"

  Thread.sleep(1000)

  system.terminate()
}

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

case class WhoToGreat(who: String)

class Greeter extends Actor with ActorLogging {
  override def receive = {
    case WhoToGreat(who) => log.info(s"Hello $who")
  }
}

object HelloAkka extends App {
  val system = ActorSystem("hello-akka")

  val greeter = system.actorOf(Props[Greeter], "greeter")

  greeter ! WhoToGreat("Bob Lob Law")

  greeter ! WhoToGreat("Bob The Builder")

  greeter ! WhoToGreat("Bob Ross")

  system.terminate()
}
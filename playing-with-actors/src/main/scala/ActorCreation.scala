import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object Apollo {
  case object Play
  case object Stop

  def props = Props[Apollo]
}

class Apollo extends Actor with ActorLogging {
  import Apollo._

  override def receive = {
    case Play => log.info(s"Music started ${"." * 15}")
    case Stop => log.info(s"Music stopped ${"." * 15}")
  }
}

object Zeus {
  case object StartMusic
  case object StopMusic
}

class Zeus extends Actor with ActorLogging {
  import Zeus._

  override def receive = {
    case StartMusic => {
      log.info("Start music")

      val apollo = context.actorOf(Apollo.props, "apollo")

      apollo ! Apollo.Play
    }
    case StopMusic => {
      log.info("Stop music")

      log.info("I don't want to stop the music.")
    }
  }
}

object ActorCreation extends App {
  val system = ActorSystem("actor-creation")

  val zeus = system.actorOf(Props[Zeus], "zeus")

  zeus ! Zeus.StartMusic

  zeus ! Zeus.StopMusic

  system.terminate()
}

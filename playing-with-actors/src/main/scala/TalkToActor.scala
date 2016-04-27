import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

case class User(username: String, email: String)

object Storage {
  case class AddUser(user: User)

  def props = Props[Storage]
}

class Storage extends Actor with ActorLogging {
  import Storage._

  var users = List.empty[User]

  override def receive = {
    case AddUser(user) => {
      log.info(s"$user stored.")

      users = user :: users
    }
  }
}

object Checker {
  case class CheckUser(user: User)

  case class WhiteUser(user: User)

  case class BlackUser(user: User)

  def props = Props[Checker]
}

class Checker extends Actor with ActorLogging {
  import Checker._

  val blacklist = Seq(
    User("Bob", "bob-the-builder@example.com"),
    User("Ross", "bob-ross@example.com")
  )

  override def receive = {
    case CheckUser(user) => {
      if (blacklist.contains(user)) {
        log.info(s"$user is in the blacklist.")

        sender ! BlackUser(user)
      } else {
        log.info(s"$user is not in the blacklist.")

        sender ! WhiteUser(user)
      }
    }
  }
}

object Recorder {
  case class NewUser(user: User)

  def props(checker: ActorRef, storage: ActorRef) = Props(new Recorder(checker, storage))
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor with ActorLogging {
  import scala.concurrent.ExecutionContext.Implicits.global

  import Recorder._
  import Checker._
  import Storage._

  implicit val timeout = Timeout(5 seconds)

  override def receive = {
    case NewUser(user) => {
      log.info(s"Receives NewUser for $user")

      checker ? CheckUser(user) map {
        case WhiteUser(user) => storage ! AddUser(user)
        case BlackUser(user) => log.info(s"$user is in the blacklist.")
      }

    }
  }
}

object TalkToActor extends App {
  val system = ActorSystem("talk-to-actor")

  val checker = system.actorOf(Checker.props, "checker")

  val storage = system.actorOf(Storage.props, "storage")

  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  recorder ! Recorder.NewUser(User("Bob", "bob-the-builder@example.com"))

  recorder ! Recorder.NewUser(User("Jane", "jane@example.com"))

  Thread.sleep(1000)

  system.terminate()
}

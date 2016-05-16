import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}
import UserStorage.DBOperation._
import UserStorage._

case class User(username: String, email: String)

object UserStorage {
  trait DBOperation

  object DBOperation {
    case object Create extends DBOperation
    case object Read extends DBOperation
    case object Update extends DBOperation
    case object Delete extends DBOperation
  }

  case object Connect
  case object Disconnect

  case class Operation(dbOperation: DBOperation, user: Option[User])
}

class UserStorage extends Actor with ActorLogging with Stash {
  override def receive = disconnected

  def connected: Actor.Receive = {
    case Disconnect => {
      log.info("UserStorage is disconnected from the DB.")

      context.unbecome()
    }
    case Operation(dbOperation, user) => {
      log.info(s"UserStorage recieve $dbOperation to do in $user")
    }
  }

  def disconnected: Actor.Receive = {
    case Connect => {
      log.info("UserStorage is connected to the DB.")

      unstashAll()

      context.become(connected)
    }
    case _ => stash()
  }
}

object BecomeHotswap extends App {
  val system = ActorSystem("become-hotswap")

  val userStorage = system.actorOf(Props[UserStorage], "user-storage")

  userStorage ! Operation(Create, Some(User("Bob", "bob-the-builder@example.com")))

  userStorage ! Connect

  userStorage ! Disconnect

  Thread.sleep(1000)

  system.terminate()
}

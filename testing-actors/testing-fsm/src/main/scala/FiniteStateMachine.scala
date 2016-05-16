import akka.actor.{ActorLogging, ActorSystem, FSM, Props, Stash}
import UserStorageFSM._
import UserStorageFSM.DBOperation._

object UserStorageFSM {
  case class User(username: String, email: String)

  sealed trait State

  case object Connected extends State
  case object Disconnected extends State

  sealed trait Data

  case object EmptyData extends Data

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

class UserStorageFSM extends FSM[State, Data] with Stash with ActorLogging {
  startWith(Disconnected, EmptyData)

  when(Disconnected) {
    case Event(Connect, _) => {
      log.info("UserStorage is connected to the DB.")

      unstashAll()

      goto(Connected) using EmptyData
    }
    case Event(_, _) => {
      stash()

      stay using EmptyData
    }
  }

  when(Connected) {
    case Event(Disconnect, _) => {
      log.info("UserStorage is disconnected from the DB.")

      goto(Disconnected) using EmptyData
    }
    case Event(Operation(dbOperation, user), _) => {
      log.info(s"UserStorage recieve $dbOperation to do in $user")

      stay using EmptyData
    }
  }

  initialize()
}

object FiniteStateMachine extends App {
  val system = ActorSystem("finite-state-machine")

  val userStorage = system.actorOf(Props[UserStorageFSM], "user-storage-fsm")

  userStorage ! Operation(Create, Some(UserStorageFSM.User("Bob", "bob-the-builder@example.com")))

  userStorage ! Connect

  userStorage ! Disconnect

  Thread.sleep(1000)

  system.terminate()
}

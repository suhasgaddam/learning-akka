import Account.{Credit, Debit, Operation}
import akka.actor.{ActorSystem, Props}

object PersistentFiniteStateMachine extends App {
  val system = ActorSystem("persisent-fsm")

  val account = system.actorOf(Props[Account], "account")

  account ! Operation(1000, Credit)

  account ! Operation(300, Debit)

  Thread.sleep(1000)

  system.terminate()
}

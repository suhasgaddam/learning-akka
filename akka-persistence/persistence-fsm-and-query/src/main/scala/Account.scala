import akka.persistence.fsm.PersistentFSM
import akka.persistence.fsm.PersistentFSM.FSMState
import Account._
import akka.actor.ActorLogging
import scala.reflect._

object Account {
  sealed trait State extends FSMState

  case object Empty extends State {
    override def identifier = "Empty"
  }

  case object Active extends State {
    override def identifier = "Active"
  }

  sealed trait Data {
    val amount: Float
  }

  case object ZeroBalance extends Data {
    override val amount = 0.0f
  }

  case class Balance(override val amount: Float) extends Data

  sealed trait DomainEvent

  case class AcceptedTransaction(amount: Float, `type`: TransactionType) extends DomainEvent
  case class RejectedTransaction(amount: Float, `type`: TransactionType, reason: String) extends DomainEvent

  sealed trait TransactionType

  case object Credit extends TransactionType
  case object Debit extends TransactionType

  case class Operation(amount: Float, `type`: TransactionType)
}

class Account extends PersistentFSM[State, Data, DomainEvent] with ActorLogging {
  override def persistenceId = "persistent-account"

  override def applyEvent(event: DomainEvent, currentData: Data): Data = {
    event match {
      case AcceptedTransaction(amount, Credit) => {
        val newAmount = currentData.amount + amount

        log.info(s"Your new balance is $newAmount")

        Balance(newAmount)
      }
      case AcceptedTransaction(amount, Debit) => {
        val newAmount = currentData.amount - amount

        log.info(s"Your new balance is $newAmount")

        if (newAmount > 0)
          Balance(newAmount)
        else
          ZeroBalance
      }
      case RejectedTransaction(_, _, reason) => {
        log.info(s"Rejected Transaction. Reason is $reason")

        currentData
      }
    }
  }

  override def domainEventClassTag = classTag[DomainEvent]

  startWith(Empty, ZeroBalance)

  when(Empty) {
    case Event(Operation(amount, Credit), _) => {
      log.info("It is your first Credit operation")

      goto(Active) applying AcceptedTransaction(amount, Credit)
    }
    case Event(Operation(amount, Debit), _) => {
      log.info("Sorry. Your account has zero balance")

      stay applying RejectedTransaction(amount, Debit, "Zero Balance")
    }
  }

  when(Active) {
    case Event(Operation(amount, Credit), _) => {
      stay applying AcceptedTransaction(amount, Credit)
    }
    case Event(Operation(amount, Debit), balance) => {
      val newBalance = balance.amount - amount

      if (newBalance > 0)
        stay applying AcceptedTransaction(amount, Debit)
      else if (newBalance == 0)
        goto(Empty) applying AcceptedTransaction(amount, Debit)
      else
        stay applying RejectedTransaction(amount, Debit, "Your balance does not cover this operation")
    }
  }
}

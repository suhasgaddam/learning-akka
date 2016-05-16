import akka.actor.ActorLogging
import akka.persistence._

object Counter {
  sealed trait Operation {
    val count: Int
  }

  case class Increment(override val count: Int) extends Operation
  case class Decrement(override val count: Int) extends Operation

  case class Command(operation: Operation)
  case class Event(operation: Operation)

  case class State(count: Int)
}

class Counter extends PersistentActor with ActorLogging {
  import Counter._

  log.info(s"Starting ${"." * 15}")

  override def persistenceId = "persistent-counter"

  var state = State(count = 0)

  def updateState(event: Event) = event match {
    case Event(Increment(count)) => {
      state = State(count = state.count + count)

      takeSnapshot()
    }
    case Event(Decrement(count)) => {
      state = State(count = state.count - count)

      takeSnapshot()
    }
  }

  override val receiveRecover: Receive = {
    case event: Event => {
      log.info(s"Counter receive $event on recovering mode.")

      updateState(event)
    }
    case SnapshotOffer(_, snapshot: State) => {
      log.info(s"Counter receive snapshot with state: $snapshot on recovering mode.")

      state = snapshot
    }
    case RecoveryCompleted => log.info("Recovery Complete. I'll switch to receiving mode.")
  }

  override val receiveCommand: Receive = {
    case command @ Command(operation) => {
      log.info(s"Counter receive $command")

      persist(Event(operation)) { event => updateState(event) }
    }
    case "print" => log.info(s"The current state of the counter is  $state")
    case SaveSnapshotSuccess(metadata) => log.info("Save Snapshot Success")
    case SaveSnapshotFailure(metadata, reason) => log.info(s"Save Snapshot Failure. Failure is $reason")
  }

  def takeSnapshot() = {
    if ((state.count % 5) == 0) {
      saveSnapshot(state)
    }
  }

//  override def recovery = Recovery.none

}

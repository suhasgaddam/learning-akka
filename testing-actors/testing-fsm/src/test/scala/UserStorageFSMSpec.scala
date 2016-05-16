import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

class UserStorageFSMSpec extends TestKit(ActorSystem("test-actor-system"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MustMatchers {

  import UserStorageFSM._
  import UserStorageFSM.DBOperation._

  override def afterAll = TestKit.shutdownActorSystem(system)

  "User Storage FSM" should "start Disconnected and EmptyData" in {
    val userStorage = TestFSMRef(new UserStorageFSM())

    userStorage.stateName must equal(Disconnected)
    userStorage.stateData must equal(EmptyData)
  }

  it should "be Connected if it receives a Connect" in {
    val userStorage = TestFSMRef(new UserStorageFSM())

    userStorage ! Connect

    userStorage.stateName must equal(Connected)
    userStorage.stateData must equal(EmptyData)
  }

  it should "still be Disconnected if it receives any other message" in {
    val userStorage = TestFSMRef(new UserStorageFSM())

    userStorage ! Create

    userStorage.stateName must equal(Disconnected)
    userStorage.stateData must equal(EmptyData)
  }

  it should "switch to Disconnected when it receives a Disconnect on Connected" in {
    val userStorage = TestFSMRef(new UserStorageFSM())

    userStorage ! Connect

    userStorage ! Disconnect

    userStorage.stateName must equal(Disconnected)
    userStorage.stateData must equal(EmptyData)
  }

  it should "still be Connected if it receives and DB operations" in {
    val userStorage = TestFSMRef(new UserStorageFSM())

    userStorage ! Connect

    userStorage ! Create

    userStorage.stateName must equal(Connected)
    userStorage.stateData must equal(EmptyData)
  }
}

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

class ParentSpec extends TestKit(ActorSystem("test-actor-system"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll = TestKit.shutdownActorSystem(system)

  "Parent Actor" should "send Ping message to child when receive Ping message" in {
    val child = TestProbe()

    val childMaker = (_: ActorRefFactory) => child.ref

    val parent = system.actorOf(Props(new Parent(childMaker)))

    parent ! Ping

    child.expectMsg(Ping)
  }
}

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

class ChildSpec extends TestKit(ActorSystem("test-actor-system"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll = TestKit.shutdownActorSystem(system)

  "Child Actor" should "send Pong message when receive Ping message" in {
    val parent = TestProbe()

    val child = system.actorOf(Props(new Child(parent.ref)))

    child ! Ping

    parent.expectMsg(Pong)
  }
}

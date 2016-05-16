import Counter.{Decrement, GetCount, Increment}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

import scala.concurrent.duration._

class CounterSpec extends TestKit(ActorSystem("test-actor-system"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll = TestKit.shutdownActorSystem(system)

  "Counter Actor" should "handle Increment message" in {
    val counter = system.actorOf(Props[Counter])

    counter ! Increment

    expectNoMsg(1 second)
  }

  it should "handle Decrement message" in {
    val counter = system.actorOf(Props[Counter])

    counter ! Decrement

    expectNoMsg(1 second)
  }

  it should "handle GetCount message" in {
    val counter = system.actorOf(Props[Counter])

    counter ! GetCount

    expectMsg(0)
  }

  it should "handle a sequence of messages" in {
    val counter = system.actorOf(Props[Counter])

    counter ! Increment

    counter ! Increment

    counter ! Decrement

    counter ! Decrement

    counter ! Increment

    counter ! Increment

    counter ! GetCount

    expectMsg(2)
  }
}

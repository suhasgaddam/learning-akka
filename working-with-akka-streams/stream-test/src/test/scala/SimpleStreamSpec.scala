import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class SimpleStreamSpec extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with FlatSpecLike
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll = TestKit.shutdownActorSystem(system)

  implicit val materializer = ActorMaterializer()

  "Simple Sink" should "return the correct result" in {
    val sink = Sink.fold[Int, Int](0)(_ + _)

    val source = Source(1 to 4)

    val result = source.runWith(sink)

    Await.result(result, 100 millis) mustEqual(10)
  }

  "Simple Source" should "contain correct elements" in {
    val source = Source(1 to 10)

    val result = source.grouped(2).runWith(Sink.head)

    Await.result(result, 100 millis) mustEqual(1 to 2)
  }

  "Simple Flow" should "do the right transformation" in {
    val source = Source(1 to 10)

    val flow = Flow[Int].takeWhile(_ < 5)

    val result = source.via(flow).runWith(Sink.fold(Seq.empty[Int])(_ :+ _))

    Await.result(result, 100 millis) mustEqual(1 to 4)
  }
}

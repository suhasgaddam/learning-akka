import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.scalalogging.LazyLogging

object Stream extends App with LazyLogging {
  implicit val system = ActorSystem()

  import system.dispatcher

  implicit val materializer = ActorMaterializer()

  val source = Source(1 to 100)

  val normalize = Flow[Int].map(_ * 2)

  val sink = Sink.foreach[Int](x => logger.info(x.toString))

  source.via(normalize).runWith(sink).andThen {
    case _ => system.terminate()
  }
}

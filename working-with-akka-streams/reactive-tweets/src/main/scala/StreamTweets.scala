import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.scalalogging.LazyLogging

object StreamTweets extends App with LazyLogging {
  implicit val system = ActorSystem()

  import system.dispatcher

  implicit val materializer = ActorMaterializer()

  val streamClient = new TwitterStreamClient(system)
  streamClient.init

  val source = Source.actorPublisher[Tweet](Props[StatusPublisher])

  val normalize = Flow[Tweet].filter {
    tweet => tweet.hashtags.contains(Hashtag("#Akka"))
  }

  val sink = Sink.foreach[Tweet](tweet => logger.info(tweet.toString))

  source.via(normalize).runWith(sink).andThen {
    case _ => system.terminate()
  }
}

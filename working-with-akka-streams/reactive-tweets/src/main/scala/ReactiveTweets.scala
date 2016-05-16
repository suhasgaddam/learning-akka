import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import twitter4j.Status

object ReactiveTweets extends App with LazyLogging {
  implicit val system = ActorSystem()

  import system.dispatcher

  implicit val materializer = ActorMaterializer()

  val source = Source(TwitterClient.retrieveTweets("#Akka"))

  val normalize = Flow[Status].map {
    tweet => Tweet(Author(tweet.getUser().getName()), tweet.getText())
  }

  val sink = Sink.foreach[Tweet](tweet => logger.info(tweet.toString))

  source.via(normalize).runWith(sink).andThen {
    case _ => system.terminate()
  }
}

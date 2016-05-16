import akka.NotUsed
import akka.actor.ActorSystem
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import akka.stream.scaladsl.Source

object PersistentQuery extends App with LazyLogging {
  val system = ActorSystem("persistent-query")

  implicit val materializer = ActorMaterializer()(system)

  val queries = PersistenceQuery(system).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)

  val events: Source[EventEnvelope, NotUsed] = queries.eventsByPersistenceId("persistent-account")

  events.runForeach { event => logger.info(s"Event: $event") }

  Thread.sleep(1000)

  system.terminate()
}

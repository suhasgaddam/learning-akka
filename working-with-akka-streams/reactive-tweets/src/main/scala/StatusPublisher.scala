import akka.stream.actor.ActorPublisher

class StatusPublisher extends ActorPublisher[Tweet] {

  val sub = context.system.eventStream.subscribe(self, classOf[Tweet])

  override def receive = {
    case tweet: Tweet => {
      if (isActive && (totalDemand > 0))
        onNext(tweet)
    }
  }

  override def postStop() = context.system.eventStream.unsubscribe(self)
}

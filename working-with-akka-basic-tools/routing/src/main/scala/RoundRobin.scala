import Worker.Work
import akka.actor.{ActorSystem, Props}
import akka.routing.{FromConfig, RoundRobinPool}

object RoundRobin extends App {
  val system = ActorSystem("round-robin-router")

  val routerPool = system.actorOf(RoundRobinPool(5).props(Props[Worker]), "round-robin-pool")

  1 to 10 foreach { _ => routerPool ! Work()}

  Thread.sleep(1000)

  val workers = List(
    "/user/worker-1",
    "/user/worker-2",
    "/user/worker-3",
    "/user/worker-4",
    "/user/worker-5"
  )

  workers.foreach( path => {
    val name = path.substring("/user/".length)

    system.actorOf(Props[Worker], name)
  })

  val routerGroup = system.actorOf(FromConfig.props(), "round-robin-group")

  1 to 10 foreach { _ => routerGroup ! Work()}

  Thread.sleep(1000)

  system.terminate()
}

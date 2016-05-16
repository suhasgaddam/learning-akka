import Worker.Work
import akka.actor.{ActorSystem, Props}
import akka.routing.{FromConfig, RandomGroup}

object RandomRouter extends App {
  val system = ActorSystem("random-router")

  val routerPool = system.actorOf(FromConfig.props(Props[Worker]), "random-router-pool")

  1 to 5 foreach { _ => routerPool ! Work()}

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

  val routerGroup = system.actorOf(RandomGroup(workers).props(), "random-router-group")

  1 to 5 foreach { _ => routerGroup ! Work()}

  Thread.sleep(1000)

  system.terminate()
}

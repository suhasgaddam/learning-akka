import Worker.Work
import akka.actor.{ActorSystem, Props}

object Router extends App {
  val system = ActorSystem("router")

  val routerPool = system.actorOf(Props[RouterPool], "router-pool")

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

  val routerGroup = system.actorOf(Props(classOf[RouterGroup], workers), "router-group")

  1 to 5 foreach { _ => routerGroup ! Work()}

  Thread.sleep(1000)

  system.terminate()
}

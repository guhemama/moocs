package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}

object StartingStoppingActors extends App {

  val system = ActorSystem("StartStop")

  object Parent {
    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging {
    import Parent._

    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        val childRef = context.actorOf(Props[Child], name)
        context.become(withChildren(children + (name -> childRef)))
      case StopChild(name) =>
        log.info(s"Stopping child $name")
        val childOption = children.get(name)
        // context.stop is an asynchronous method.
        childOption.foreach(childRef => context.stop(childRef))
      case Stop =>
        log.info("Stopping parent")
        // Stops itself and all its children.
        context.stop(self)
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  import Parent._

  val parent = system.actorOf(Props[Parent], "parent")
  parent ! StartChild("child1")

  val child = system.actorSelection("/user/parent/child1")
  child ! "It works!"

  parent ! StopChild("child1")

  parent ! Stop

  val looseActor = system.actorOf(Props[Child])
  // PoisonPill kills an actor.
  looseActor ! PoisonPill

  val looseActor2 = system.actorOf(Props[Child])
  // Kill kills an actor.
  looseActor2 ! Kill

  /**
    * Watch a stopped actor. You can be notified when an actor is stopped.
    */
  class Watcher extends Actor with ActorLogging {
    import Parent._

    override def receive: Receive = {
      case StartChild(name) =>
        val child = context.actorOf(Props[Child], name)
        log.info(s"Started and watching child $name")
        context.watch(child)
      case Terminated(ref) =>
        log.info(s"$ref has been stopped")
    }
  }

  val watcher = system.actorOf(Props[Watcher], "watcher")
  watcher ! StartChild("watchedChild")
  val watchedChild = system.actorSelection("/user/watcher/watchedChild")
  Thread.sleep(500)
  watchedChild ! PoisonPill
}

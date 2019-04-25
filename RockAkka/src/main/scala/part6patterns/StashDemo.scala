package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}

/**
  * Stash lets you put messages aside for later.
  */
object StashDemo extends App {
  /**
    * Resource actor: it handles a file, and therefore its write/read requests.
    *
    * States:
    *
    * ResourceActor is closed
    * - Open => switch to the open state.
    * - Read, Write are postponed
    *
    * ResourceActor is open
    * - Read, Write are handled.
    * - Close => switch to closed state.
    */
  case object Open
  case object Close
  case object Read
  case class Write(data: String)

  // Step 1: mix-in the Stash trait.
  class ResourceActor extends Actor with ActorLogging with Stash {
    private var data: String = ""

    override def receive: Receive = closed

    def closed: Receive = {
      case Open =>
        log.info("Opening resource")
        // Step 3: unstashAll when you switch the message handler
        unstashAll()
        context.become(open)
      case message =>
        log.info(s"Stashing $message")
        // Step 2: stash away what you cannot handle
        stash()
    }

    def open: Receive = {
      case Read =>
        log.info(s"I have read $data")
      case Write(newData) =>
        log.info(s"I am writing $newData")
        data = newData
      case Close =>
        log.info("Closing resource")
        context.become(closed)
      case message =>
        log.info(s"Stashing $message because I cannot handle it")
        stash()
    }
  }

  val system = ActorSystem("StashDemo")
  val actor = system.actorOf(Props[ResourceActor], "ResourceActor")

  actor ! Write("foo")
  actor ! Read
  actor ! Open
  actor ! Close
}

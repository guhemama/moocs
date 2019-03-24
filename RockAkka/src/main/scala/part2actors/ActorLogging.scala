package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

/**
  * Logging is done asynchronously.
  */
object ActorLogging extends App {
  /**
    * An explicit logger can be used for logging.
    */
  class ActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)

    override def receive: Receive = {
      case message => logger.info(message.toString)
    }
  }

  /**
    * Logging with ActorLogging. It works the same way as the
    * explicit version.
    */
  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("logger")

  val explicit = system.actorOf(Props[ActorWithExplicitLogger], "explicit")
  explicit ! "OMG barbecue!"

  val withLogging = system.actorOf(Props[ActorWithLogging], "withLogging")
  withLogging ! "It works again!"
}

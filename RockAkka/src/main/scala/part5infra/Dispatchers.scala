package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
  * Dispatches control how messages are send and handled.
  */
object Dispatchers extends App {
  class Counter extends Actor with ActorLogging {
    var count = 0

    override def receive: Receive = {
      case message =>
        count += 1
        log.info(s"[$count] $message")
    }
  }

  val system = ActorSystem(
    "Dispatchers",
    ConfigFactory.load().getConfig("dispatchers")
  )

  // Use dispatcher form config (programatically)
  val actors = for(i <- 1 to 10) yield {
    system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"counter$i")
  }

  // Each actor will process 30 messages at a time (throughput setting)
  val r = new Random()
  for (i <- 1 to 1000) {
    actors(r.nextInt(10)) ! i
  }

  // Use dispatcher from config (akka.deployment)
  val dActor = system.actorOf(Props[Counter], "dbactor")

  /**
    * Dispatchers implements the ExecutionContext trait
    */
  class DatabaseActor extends Actor with ActorLogging {
    implicit val executionContext: ExecutionContext =
      context.system.dispatchers.lookup("my-dispatcher")

    override def receive: Receive = {
      case message => Future {
        // Does heavy computation
        Thread.sleep(5000)
        log.info(s"Success: $message")
      }
    }
  }

  val dbActor = system.actorOf(Props[DatabaseActor])
  dbActor ! "Meaning of life: 42"
}

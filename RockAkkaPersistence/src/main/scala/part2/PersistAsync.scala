package part2

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistAsync extends App {

  case class Event(contents: String)
  case class Command(contents: String)

  object PersistAsyncActor {
    def props(eventAggregator: ActorRef) = Props(new PersistAsyncActor(eventAggregator))
  }

  class PersistAsyncActor(eventAggregator: ActorRef) extends PersistentActor with ActorLogging {
    override def persistenceId: String = "persist-async"

    override def receiveCommand: Receive = {
      case Command(contents) =>
        eventAggregator ! s"Processing $contents"
        persistAsync(Event(contents)) { e =>
          eventAggregator ! e
        }

        persist(Event(contents)) { e =>
          log.info(s"Doing stuff with $e")
        }
    }

    override def receiveRecover: Receive = {
      case message => log.info(s"Recovered: $message")
    }
  }

  class EventAggregator extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(s"Aggregating $message")
    }
  }

  val system = ActorSystem("PersistAsync")
  val eventAggr = system.actorOf(Props[EventAggregator], "eventAggr")
  val actor = system.actorOf(PersistAsyncActor.props(eventAggr), "persistAsync")

  actor ! Command("Command 1")
  actor ! Command("Command 2")
}

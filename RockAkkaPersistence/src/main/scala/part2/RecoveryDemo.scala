package part2

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, Recovery, RecoveryCompleted}

object RecoveryDemo extends App {

  case class Command(contents: String)
  case class Event(contents: String)

  class RecoveryActor extends PersistentActor with ActorLogging {
    override def persistenceId: String = "recovery-actor"

    override def receiveCommand: Receive = {
      case Command(contents) =>
        persist(Event(contents)) { e =>
          log.info(s"Successfully persisted $e")
        }
    }

    override def receiveRecover: Receive = {
      case RecoveryCompleted =>
        log.info("Recovery is complete")
      case Event(contents) =>
        log.info(s"Recovered $contents")
    }

    /**
      * This is called when recovery fails.
      *
      * @param cause
      * @param event
      */
    override def onRecoveryFailure(cause: Throwable, event: Option[Any]): Unit = {
      log.error("The recovery failed.")
      super.onRecoveryFailure(cause, event)
    }

    override def recovery: Recovery = super.recovery
  }

  val system = ActorSystem("RecoveryDemo")
  val actor = system.actorOf(Props[RecoveryActor], "recoveryActor")

  /**
    * All commands send during recovery are stashed. If you run this
    * twice, you will be able to reproduce that.
    */
  for (i <- 1 to 1000) {
    actor ! Command(s"Command $i")
  }
}

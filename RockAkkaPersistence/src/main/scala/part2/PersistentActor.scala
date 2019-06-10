package part2

import akka.actor.{ActorLogging, ActorSystem, PoisonPill, Props}
import akka.persistence.PersistentActor
import java.util.Date

object PersistentActor extends App {
  /**
    * Scenario: we have a business and an accountant which keeps track of our invoices.
    */
  // Command definitions
  case class Invoice(recipient: String, date: Date, amount: Int)
  case class InvoiceBulk(invoices: List[Invoice])
  case object Shutdown

  // Event definitions
  case class InvoiceRecorded(id: Int, recipient: String, date: Date, amount: Int)

  class Accountant extends PersistentActor with ActorLogging {
    // Vars for learning purposes
    var latestInvoiceId = 0
    var totalAmount = 0

    /**
      * Best practice: make it unique
      * @return
      */
    override def persistenceId: String = "persistent-accountant"

    /**
      * This is a normal receive method.
      * When you receive a command:
      * 1. You create an event to persist into the store
      * 2. You persist the event, and pass a callback that will be triggered
      * @return
      */
    override def receiveCommand: Receive = {
      case Invoice(recipient, date, amount) =>
        log.info(s"Received invoice for amount: $amount")
        persist(InvoiceRecorded(latestInvoiceId, recipient, date, amount)) { e =>
          latestInvoiceId += 1
          totalAmount += amount
          log.info(s"Persisted $e as invoice #${e.id}, for total amount $totalAmount")
        }
      // Persisting multiple messages
      case InvoiceBulk(invoices) =>
        val invoiceIds = latestInvoiceId to (latestInvoiceId + invoices.size)

        // Create the events
        val events = invoices.zip(invoiceIds).map { pair =>
          val invoice = pair._1
          val id = pair._2

          InvoiceRecorded(id, invoice.recipient, invoice.date, invoice.amount)
        }

        persistAll(events) { e =>
          latestInvoiceId += 1
          totalAmount += e.amount
        }
      case Shutdown =>
        context.stop(self)
    }

    /**
      * Handle that will be called on recovery
      * @return
      */
    override def receiveRecover: Receive = {
      /**
        * Best practice: follow the logic in the persist steps of receiveCommand, which will rebuilt the last events (Event Sourcing)
        */
      case InvoiceRecorded(id, _, _, amount) => {
        log.info(s"Recovered invoice #$id for amount $amount, total amount: $totalAmount")
        latestInvoiceId = id
        totalAmount += amount
      }
    }

    override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"Failed to persist $event because of $cause")
      super.onPersistFailure(cause, event, seqNr)
    }

    override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"Persist $event rejected because of $cause")
      super.onPersistRejected(cause, event, seqNr)
    }
  }

  val system = ActorSystem("persistentActors")
  val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

//  for (i <- 1 to 10) {
//    accountant ! Invoice("The Sofa Company", new Date, i * 1000)
//  }

  val invoices = for (i <- 1 to 5) yield Invoice("Awesome Chairs", new Date, i * 100)
  accountant ! InvoiceBulk(invoices.toList)

  /**
    * Kill the actor. With PoisonPill no messages will be delivered (dead letters). It is better to implement your own "shutdown" message.
    */
  //accountant ! PoisonPill
  accountant ! Shutdown
}

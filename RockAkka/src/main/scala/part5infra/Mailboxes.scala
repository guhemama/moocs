package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

object Mailboxes extends App {
  val system = ActorSystem("Mailbox", ConfigFactory.load().getConfig("mailboxes"))

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /**
    * #1 - A custom priority mailbox
    * P0 -> most important
    * P1
    * P2
    * P3 -> least important
    */
  // Step 1: define the mailbox
  class SupportTicketPriorityMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(PriorityGenerator {
    case message: String if message.startsWith("P0") => 0
    case message: String if message.startsWith("P1") => 1
    case message: String if message.startsWith("P2") => 2
    case message: String if message.startsWith("P3") => 3
    case _ => 4
  })

  // Step 2: make it know on the config
  // Step 3: attach the dispatcher to an actor
  val supportTicketActor = system.actorOf(Props[SimpleActor].withDispatcher("support-ticket-dispatcher"))

  // The messages should be handled in our custom order.
  // Poison pill gets priority 4.
  supportTicketActor ! PoisonPill
  supportTicketActor ! "P3 Nice to have"
  supportTicketActor ! "P0 It is broken, halp"
  supportTicketActor ! "P1 We need help"

  /**
    * #2 - Control-aware mailbox
    * We will use UnboundedControlAwareMailbox
    */
  // Step 1: mark a message as a control message (prioritised)
  case object ManagementTicket extends ControlMessage

  // Step 2: configure who gets the mailbox
  // Make the actor attach to the mailbox.
  val controlAwareActor = system.actorOf(Props[SimpleActor].withMailbox("control-mailbox"))

  // ManagementTicket is received first.
//  controlAwareActor ! "P0 It is broken, halp"
//  controlAwareActor ! "P1 We need help"
//  controlAwareActor ! ManagementTicket

  // We can also use the deployment config
  val anotherControlAwareActor = system.actorOf(Props[SimpleActor], "anotherControlAwareActor")
  anotherControlAwareActor ! "P0 It is broken, halp"
  anotherControlAwareActor ! "P1 We need help"
  anotherControlAwareActor ! ManagementTicket
}

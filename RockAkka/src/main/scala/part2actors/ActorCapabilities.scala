package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ActorCapabilities.BankOps.TransactionSuccess

import scala.util.{Failure, Success}

object ActorCapabilities extends App {
  // Every actor has access to a context: `context`
  // - context.self => actor reference
  // - context.sender => contains the reference to the last actor
  //   who sent a message.

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi" => context.sender() ! "Hello, there!" // Replying to a message
      case message: String => println(s"[SimpleActor] I have received: $message")
      case number: Int => println(s"[SimpleActor] I have received a number: $number")
      case SpecialMessage(content) => println(s"[SimpleActor] I have received a special message: $content")
      case SayHiTo(ref) => ref ! "Hi"
    }
  }

  val actorSystem = ActorSystem("actorCapabilities")
  val simpleActor = actorSystem.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "Hello, actor!"

  // Messages can be of any type - if they pattern match.
  // a. Messages must be immutable.
  // b. Messages must be serializable.
  simpleActor ! 42

  case class SpecialMessage(content: String)
  simpleActor ! SpecialMessage("OMG secret!")

  // Actors can reply to messages.
  val alice = actorSystem.actorOf(Props[SimpleActor], "alice")
  val bob = actorSystem.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)

  alice ! SayHiTo(bob)

  /**
    * 1. Implement a counter actor:
    *   - Increment message
    *   - Decrement message
    *   - Print message
    */
  class CounterActor extends Actor {
    var count: Int = 0

    override def receive: Receive = {
      case Decrement => count -= 1
      case Increment => count += 1
      case Print     => println(s"[${context.self.path.name}] ${count}")
    }
  }

  trait CounterOps
  case object Decrement extends CounterOps
  case object Increment extends CounterOps
  case object Print extends CounterOps

  val counterActor = actorSystem.actorOf(Props[CounterActor], "counter")

  counterActor ! Increment
  counterActor ! Increment
  counterActor ! Print
  counterActor ! Decrement
  counterActor ! Print

  /**
    * 2. Create a bank account as an actor.
    * It receives:
    * - Deposit an amount
    * - Withdraw an amount
    * - Statement
    * and replies with:
    * - Success
    * - Failure
    */
  object BankOps {
    case class Deposit(amount: Double)
    case class Withdraw(amount: Double)
    case object Statement
    trait TransactionResult
    case class TransactionSuccess(message: String) extends TransactionResult
    case class TransactionFailure(message: String) extends TransactionResult
  }

  class BankAccount extends Actor {
    import BankOps._

    @volatile
    var balance: Double = 0.00

    override def receive: Receive = {
      case Deposit(amount) => context.sender() ! deposit(amount)
      case Withdraw(amount) => context.sender() ! withdraw(amount)
      case Statement => println(s"Your balance is $balance")
    }

    def deposit(d: Double): TransactionResult =
      if (d < 0.00) {
        TransactionFailure("Deposit must be bigger than 0.00")
      } else {
        balance += d
        TransactionSuccess("Money was deposited")
      }

    def withdraw(d: Double): TransactionResult =
      if (d > balance) {
        TransactionFailure("Balance is too low")
      } else {
        balance -= d
        TransactionSuccess("Money was withdrawn")
      }
  }

  object Customer {
    case class DoBankTransactions(account: ActorRef)
  }

  class Customer extends Actor {
    import Customer._
    import BankOps._

    override def receive: Receive = {
      case DoBankTransactions(account) =>
        account ! Deposit(100)
        account ! Withdraw(30)
        account ! Withdraw(5)
        account ! Statement
      case message => println(message.toString)
    }
  }

  val account = actorSystem.actorOf(Props[BankAccount], "account")
  val customer = actorSystem.actorOf(Props[Customer], "customer")

  customer ! Customer.DoBankTransactions(account)
}

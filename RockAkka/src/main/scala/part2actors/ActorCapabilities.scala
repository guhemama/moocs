package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

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
}

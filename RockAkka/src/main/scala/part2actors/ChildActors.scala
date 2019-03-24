package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChildActors.Parent.{CreateChild, TellChild}

object ChildActors extends App {
  /**
    * Actors can create other actors.
    */

  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor {
    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"[${self.path}] Creating child")
        val childRef = context.actorOf(Props[Child], name)
        context.become(withChild(childRef))
    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) => childRef.forward(message)
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"[${self.path}] I got: $message")
    }
  }

  val system = ActorSystem("ParentChild")
  val parent = system.actorOf(Props[Parent], "parent")

  parent ! CreateChild("Bobby")
  parent ! TellChild("Eins Zwei Drei")

  /**
    * This allows us to implement actor hierarchies.
    * An actor always jusbelongs to a parent.
    *
    * There are three guardian actors:
    * - root (/)
    * - user (/user)
    * - system (/system)
    *
    * It is not advisable to pass mutable data to child actors.
    * This will break encapsulation.
    */

  /**
    * We can locate actors by using their path.
    */
  val childSelection = system.actorSelection("/user/parent/Bobby")
  childSelection ! "I found you!"
}

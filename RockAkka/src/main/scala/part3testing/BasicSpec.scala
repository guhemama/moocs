package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  import BasicSpec._

  /**
    * Clean up after tests.
    */
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A simple actor" should {
    "send back the same message" in {
      val actor = system.actorOf(Props[SimpleActor])
      val message = "Echoooo!"
      actor ! message

      expectMsg(message)
    }
  }

  "A blackhole actor" should {
    "should not send back any message" in {
      val actor = system.actorOf(Props[Blackhole])
      val message = "Echoooo!"
      actor ! message

      expectNoMessage(1 second)
    }
  }

  "A special actor" should {
    val actor = system.actorOf(Props[SpecialActor])

    "uppercase the message" in {
      actor ! "I love Akka"
      val reply = expectMsgType[String]

      assert(reply == "I LOVE AKKA")
    }

    "reply with a greeting" in {
      actor ! "greeting"
      expectMsgAnyOf("hi", "hallo")
    }

    "reply with favorite tech" in {
      actor ! "favoriteTech"
      expectMsgAllOf("Scala", "Akka")
    }

    "reply with cool tech in a different way" in {
      actor ! "favoriteTech"
      val messages = receiveN(2) // Seq[Any]

      // Can now do more complicated assertions...
    }

    "reply with cool tech in a fancy way" in {
      actor ! "favoriteTech"

      // Expects a partial function
      expectMsgPF() {
        case "Scala" =>
        case "Akka" =>
      }
    }
  }
}

/**
  * Stores all the values we are going to use on our tests.
  */
object BasicSpec {
  // Simple actor that replies with the same message
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class Blackhole extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class SpecialActor extends Actor {
    val random = new Random()

    override def receive: Receive = {
      case "greeting" =>
        sender() ! (if (random.nextBoolean()) "hi" else "hallo")
      case "favoriteTech" =>
        sender() ! "Scala"
        sender() ! "Akka"
      case message: String => sender() ! message.toUpperCase()
    }
  }
}
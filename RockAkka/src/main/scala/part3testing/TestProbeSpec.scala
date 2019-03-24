package part3testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register a slave" in {
      val master = system.actorOf(Props[Master])

      // A test probe is a special actor with assertion capabilities.
      // It can also send messages.
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)
    }

    "send the work to the slave" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workString = "I Love Akka <3"
      master ! Work(workString)

      slave.expectMsg(SlaveWork(workString, testActor))
      slave.reply(WorkCompleted(3, testActor))

      expectMsg(Report(3))
    }

    "aggregate data correctly" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workString = "I Love Akka <3"
      master ! Work(workString)
      master ! Work(workString)

      slave.receiveWhile() {
        case SlaveWork(`workString`, `testActor`) => slave.reply(WorkCompleted(3, testActor))
      }

      expectMsg(Report(3))
      expectMsg(Report(6))
    }
  }
}

object TestProbeSpec {
  /**
    * Test scenario: word counting actor hierarchy.
    * Work => Master => Slave => Master => Aggregate => Response
    */
  case class Register(slaveRef: ActorRef)
  case class Work(text: String)
  case class SlaveWork(text: String, originalRequester: ActorRef)
  case class WorkCompleted(count: Int, originalRequester: ActorRef)
  case class Report(total: Int)
  case object RegistrationAck

  class Master extends Actor {
    override def receive: Receive = {
      case Register(slaveRef) =>
        sender() ! RegistrationAck
        context.become(online(slaveRef, 0))
      case _ => // Do nothing
    }

    def online(slaveRef: ActorRef, totalWordCount: Int): Receive = {
      case Work(text) => slaveRef ! SlaveWork(text, sender())
      case WorkCompleted(count, originalRequester) =>
        val newTotalWordCount = count + totalWordCount
        originalRequester ! Report(newTotalWordCount)
        context.become(online(slaveRef, newTotalWordCount))
    }
  }

  // No need to implement it.
  class Slave extends Actor {
    override def receive: Receive = ???
  }
}

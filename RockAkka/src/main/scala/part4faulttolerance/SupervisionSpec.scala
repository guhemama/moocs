package part4faulttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, AllForOneStrategy, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

/**
  * Supervisor Strategy: parents decide on their children's failure
  * with a supervision strategy.
  */
class SupervisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
with ImplicitSender with WordSpecLike with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import SupervisionSpec._

  "A supervisor" should {
    "resume its child in case of a minor fault" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      // The actor should be resumed
      child ! "This is a really long message that will crash the actor"
      child ! Report
      expectMsg(3)
    }

    "restart its child in case of an empty sentence" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      // The actor should be restarted
      child ! ""
      child ! Report
      expectMsg(0)
    }

    "terminate its child in case of a major fault" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! "cheesecake"

      // Checks if the terminated actor matches the child
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }

    "escalate an error when it does not know what to do" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! 42

      // Everything crashes when escalating.
      // Checks if the terminated actor matches the child
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }
  }

  "A no-death supervisor" should {
    "not kill children when it is restarted" in {
      val supervisor = system.actorOf(Props[NoDeathOnRestartSupervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "Akka is cool"
      child ! Report
      expectMsg(3)

      // Crash it, but the supervisor will not kill it.
      child ! 42

      // The child will still be alive, but restarted.
      child ! Report
      expectMsg(0)
    }
  }

  "An all-for-one supervisor" should {
    "apply the all-for-one strategy" in {
      val supervisor = system.actorOf(Props[AllForOneSupervisor], "AllForOneSupervisor")
      supervisor ! Props[FussyWordCounter]
      val child1 = expectMsgType[ActorRef]

      supervisor ! Props[FussyWordCounter]
      val child2 = expectMsgType[ActorRef]

      child2 ! "Test supervision"
      child2 ! Report
      expectMsg(2)

      // First child throws exception, but all children will be restarted.
      // EventFilter assures us that the exception is thrown.
      EventFilter[NullPointerException]() intercept {
        child1 ! ""
      }

      // Both children are restarted, so child2 is back to 0.
      child2 ! Report
      expectMsg(0)
    }
  }
}

object SupervisionSpec {
  class Supervisor extends Actor {
    /**
      * Here we will handle the exceptions on the supervisor
      * strategy. Restart, Stop, etc are Directives.
      * Only the affected child is subject to the strategy.
      */
    override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }

    override def receive: Receive = {
      // Spawn a new child actor
      case props: Props =>
        val childRef = context.actorOf(props)
        sender() ! childRef
    }
  }

  class NoDeathOnRestartSupervisor extends Supervisor {
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      // Do nothing.
    }
  }

  class AllForOneSupervisor extends Supervisor {
    /**
      * In this case, all of the children are subject to this strategy,
      * even if they did not cause the exception.
      */
    override val supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }
  }

  case object Report

  class FussyWordCounter extends Actor {
    var words = 0

    // We will throw exceptions to test supervision.
    override def receive: Receive = {
      case Report => sender() ! words
      case "" => throw new NullPointerException("Sentence is empty")
      case sentence: String =>
        if (sentence.length > 20) throw new RuntimeException("Sentence is too big")
        else if (Character.isUpperCase(sentence(0)) == false) throw new IllegalArgumentException("Sentence must start with uppercase letter")
        else words += sentence.split(" ").length
      case _ => throw new Exception("Can only receive strings")
    }
  }
}

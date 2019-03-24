package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

class TimeAssertionsSpec extends TestKit(ActorSystem("TimeAssertSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TimeAssertionsSpec._

  "A worker actor" should {
    val workerActor = system.actorOf(Props[WorkerActor])

    "reply with the meaning of life in a timely manner" in {
      // The response should be receive after 500ms but before 1s
      within(500 millis, 1 second) {
        workerActor ! "work"
        expectMsg(WorkResult(42))
      }
    }

    "reply with valid results at a reasonable cadence" in {
      within(1 second) {
        workerActor ! "workSequence"
        val results: Seq[Int] = receiveWhile[Int](2 seconds, 500 millis, 10) {
          case WorkResult(result) => result
        }

        assert(results.sum > 5)
      }
    }

    "reply to a test probe in a timely manner" in {
      within(1 second) {
        val probe = TestProbe()
        probe.send(workerActor, "work")
        probe.expectMsg(WorkResult(42))
      }
    }
  }
}

object TimeAssertionsSpec {
  case class WorkResult(result: Int)

  // Here we create the test scenario.
  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        // Long computation
        Thread.sleep(500)
        sender() ! WorkResult(42)
      case "workSequence" =>
        val r = new Random()
        for (_ <- 1 to 10) {
          Thread.sleep(r.nextInt(50))
          sender() ! WorkResult(1)
        }
    }
  }
}
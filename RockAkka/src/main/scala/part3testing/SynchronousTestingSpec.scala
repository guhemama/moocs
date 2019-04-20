package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.TestActorRef
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SynchronousTestingSpec extends WordSpecLike with BeforeAndAfterAll {
  implicit val system = ActorSystem("SynchronousTestingSpec")

  override def afterAll(): Unit = {
    system.terminate()
  }

  import SynchronousTestingSpec._

  "A counter" should {
    "synchronously increase its counter" in {
      // TestActorRef is responsible for the magic
      val counter = TestActorRef[Counter](Props[Counter])
      // This is sent synchronously.
      counter ! Inc

      // Allows the inspection of the actor
      assert(counter.underlyingActor.count == 1)
    }

    "synchronously increase its counter at the call of receive" in {
      val counter = TestActorRef[Counter](Props[Counter])
      counter.receive(Inc)
      assert(counter.underlyingActor.count == 1)
    }
  }
}

object SynchronousTestingSpec {
  case object Inc
  case object Read
  class Counter extends Actor {
    var count = 0

    override def receive: Receive = {
      case Inc => count += 1
      case Read => sender() ! count
    }
  }
}

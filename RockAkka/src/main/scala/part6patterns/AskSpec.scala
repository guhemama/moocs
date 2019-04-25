package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

// Step 1: import the ask pattern
import akka.pattern.ask

class AskSpec extends TestKit(ActorSystem("AskSpec"))
  with ImplicitSender with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import AskSpec._

  "An authenticate" should {
    "fail to authenticate a non-registered user" in {
      val authManager = system.actorOf(Props[AuthManager])
      authManager ! Authenticate("MaxMustermann", "123456")
      expectMsg(AuthFailure("System failure"))
    }

    "fail to authenticate if the password is invalid" in {
      val authManager = system.actorOf(Props[AuthManager])
      authManager ! RegisterUser("MaxMustermann", "123456")
      authManager ! Authenticate("MaxMustermann", "abcdef")
      expectMsg(AuthFailure("Incorrect password"))
    }
  }
}

object AskSpec {
  // This is some code that was on your app.
  case class Read(key: String)
  case class Write(key: String, value: String)
  class KeyValueActor extends Actor with ActorLogging {
    override def receive: Receive = online(Map())

    def online(map: Map[String, String]): Receive = {
      case Read(key) =>
        log.info(s"Trying to read value with key $key")
        sender() ! map.get(key)
      case Write(key, value) =>
        log.info(s"Writing the value $value for the key $key")
        context.become(online(map + (key -> value)))
    }
  }

  // User authenticator author that uses the KV actor store
  case class RegisterUser(username: String, password: String)
  case class Authenticate(username: String, password: String)
  case class AuthFailure(message: String)
  case object AuthSuccess
  class AuthManager extends Actor with ActorLogging {
    protected val database = context.actorOf(Props[KeyValueActor])

    // Step 2: setup infrastructure
    implicit val timeout: Timeout = Timeout(1 second)
    implicit val executionContext: ExecutionContext = context.dispatcher

    override def receive: Receive = {
      case RegisterUser(username, password) =>
        database ! Write(username, password)
      case Authenticate(username, password) =>
        // Step 3: use the ask (?) method, which returns a Future
        val future = database ? Read(username)

        // Save the original sender, because it could change before
        // the future is complete.
        val originalSender = sender()

        future.onComplete {
          case Success(None) =>
            originalSender ! AuthFailure("Password not found")
          case Success(Some(storedPassword)) =>
            if (storedPassword == password) originalSender ! AuthSuccess
            else originalSender ! AuthFailure("Incorrect password")
          case Failure(_) =>
            originalSender ! AuthFailure("System error")
        }
    }
  }
}
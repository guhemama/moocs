package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Introduction to Akka configuration.
  */
object IntroAkkaConfig extends App {
  class LoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /**
    * 1. Inline configuration
    */
  val inlineConfig =
    """
      | akka {
      |   loglevel = "DEBUG"
      | }
    """.stripMargin

  val config = ConfigFactory.parseString(inlineConfig)
  val system = ActorSystem("ConfigDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[LoggingActor])

  actor ! "This will be logged"

  /**
    * 2. Configuration in a file
    *
    * Generally stored in src/main/resources/application.conf
    */
  val system2 = ActorSystem("ConfigDemo2")
  val actor2 = system2.actorOf(Props[LoggingActor])

  actor2 ! "Will this be logged?"

  /**
    * 3. Separate config in the same file (on the namespace
    * 'specialConfig' on this example)
    */
  val specialConfig = ConfigFactory.load().getConfig("specialConfig")
  val system3 = ActorSystem("ConfigDemo3", specialConfig)
  val actor3 = system3.actorOf(Props[LoggingActor])

  actor3 ! "Wow no log"

  /**
    * 4. Separate config on separate file
    */
  val specialConfigFile = ConfigFactory.load("secret.conf")
  val system4 = ActorSystem("ConfigDemo4", specialConfigFile)
  val actor4 = system4.actorOf(Props[LoggingActor])

  actor4 ! "Logging again!"

  /**
    * 5. Different file formats such as JSON or YAML can be used.
    */
}

package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {
  // Every app starts with an actor system
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  /**
    * Actors are uniquely identified.
    * Messages are asynchronous.
    * Each actor may respond differently.
    * Actors are encapsulated.
    */
  // Example: word count actor
  class WordCountActor extends Actor {
    // Internal data (encapsulated)
    var totalWords = 0

    // The actor behaviour.
    // Receive === PartialFunction[Any, Unit]
    def receive: Receive = {
      case message: String =>
        println(s"[WordCounter] I have received: '$message'.")
        totalWords += message.split(" ").length
      case message => println(s"[WordCounter] I cannot understand ${message.toString}.")
    }
  }

  // Instantiate our actor.
  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")

  // Communicate with our actor. ! is also called "tell".
  wordCounter ! "I am learning Akka and it's pretty damn cool!"

  // Passing arguments to an actor is better done via a
  // companion object.
  object Person {
    // This is a factory method that creates actors.
    def props(name: String) = Props(new Person(name))
  }

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "Hi" => println(s"Hi, my mane is $name.")
    }
  }

  val person = actorSystem.actorOf(Person.props("Bob"))
  person ! "Hi"
}

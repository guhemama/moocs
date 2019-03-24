package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorsExercise extends App {
  /**
    * Distributed word counting with child actors.
    */
  object WordCounter {
    case class Initialize(numberOfChildren: Int)
    case class WordCountTask(text: String)
    case class WordCountReply(text: String, count: Int)
  }

  class WordCounter extends Actor {
    import WordCounter._

    override def receive: Receive = {
      case Initialize(numberOfChildren) =>
        println(s"[${self.path}] initializing children")

        val children = for (i <- 1 to numberOfChildren)
          yield context.actorOf(Props[WordCounterWorker], s"worker${i}")

        context.become(distributeWork(children, 0))
    }

    def distributeWork(children: Seq[ActorRef], currentChild: Int): Receive = {
      case WordCountTask(text) =>
        println(s"[${self.path}] received task")
        children(currentChild) ! text
        context.become(distributeWork(children, nextChildActor(children.length, currentChild)))
      case WordCountReply(text, count) =>
        println(s"[${self.path}] '$text' has $count words.")
    }

    def nextChildActor(total: Int, current: Int): Int = {
      (current + 1) % total
    }
  }

  class WordCounterWorker extends Actor {
    import WordCounter._

    override def receive: Receive = {
      case text: String =>
        println(s"[${self.path}] is counting")
        context.sender() ! WordCountReply(text, countWords(text))
    }

    def countWords(text: String): Int = {
      text.split(" ").map(_.toLowerCase).length
    }
  }

  val system = ActorSystem.create("WordCounter")
  val wordCounter = system.actorOf(Props[WordCounter], "master")

  wordCounter ! WordCounter.Initialize(10)
  wordCounter ! WordCounter.WordCountTask("O rato roeu a roupa do Rei de Roma.")
  wordCounter ! WordCounter.WordCountTask("Ein Jodler hör ich gern.")
  wordCounter ! WordCounter.WordCountTask("Lorem ipsum.")
}

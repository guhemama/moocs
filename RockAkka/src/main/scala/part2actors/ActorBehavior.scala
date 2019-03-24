package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * How to change actor behaviour programmatically.
  */
object ActorBehavior extends App {

  object FussyKid {
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor {
    import FussyKid._
    import Mom._

    // The internal state of the kid
    private var state = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  /**
    * context.become(method) changes the Actor's behavior
    * to become the new 'Receive'.
    */
  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive

    // This represents the "happy" state
    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive) // Forward to the other handler
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    // This represents the "sad" state
    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome
      case Ask(_) => sender() ! KidReject
    }
  }

  object Mom {
    case class MomStart(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String)
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mom extends Actor {
    import Mom._
    import FussyKid._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("Do you want to play?")
      case KidAccept => println("Yay, my kid is happy!")
      case KidReject => println("My kid is sad, but at he is least healthy.")
    }
  }

  import Mom._
  import FussyKid._

  val system = ActorSystem("actorBehaviour")
  val kid = system.actorOf(Props[FussyKid])
  val statelessKid = system.actorOf(Props[StatelessFussyKid])
  val mom = system.actorOf(Props[Mom])

  mom ! MomStart(statelessKid)

  /**
    * Exercises.
    * 1 - Recreate the Counter Actor with context.become and no mutable state.
    */
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    import Counter._

    override def receive: Receive = countReceive(0)

    // The handler becomes the same function but with a different argument.
    def countReceive(currentCount: Int): Receive = {
      case Increment =>
        println(s"[counter] $currentCount incrementing")
        context.become(countReceive(currentCount + 1))
      case Decrement =>
        println(s"[counter] $currentCount decrementing")
        context.become(countReceive(currentCount - 1))
      case Print => println(s"[counter] my current count is $currentCount")
    }
  }

  import Counter._
  val counter = system.actorOf(Props[Counter], "myCounter")

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

  /**
    * 2. Simplified voting system.
    */
  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])

  class Citizen extends Actor {
    override def receive: Receive = {
      case Vote(c) => context.become(voted(c))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(candidate: String): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])

  class VoteAggregator extends Actor {
    override def receive: Receive = awaiting

    def awaiting: Receive = {
      case AggregateVotes(citizens) =>
        citizens.foreach(citizenRef => citizenRef ! VoteStatusRequest)
        context.become(awaitingStatuses(citizens, Map()))
    }

    def awaitingStatuses(citizens: Set[ActorRef], currentStats: Map[String, Int]): Receive = {
      case VoteStatusReply(None) =>
        // Citizen has not voted yet
        sender() ! VoteStatusRequest // This may end in an infinite loop
      case VoteStatusReply(Some(candidate)) =>
        val citizensLeftWaiting = citizens - sender()
        val currentVotes = currentStats.getOrElse(candidate, 0)
        val newStats = currentStats + (candidate -> (currentVotes + 1))

        if (citizensLeftWaiting.isEmpty) {
          println(s"[VotesAggregator] Pool stats: $newStats")
        } else {
          context.become(awaitingStatuses(citizensLeftWaiting, newStats))
        }
    }
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

  /**
    * Prints the status of the votes.
    * Map("Martin" -> 1, "Jonas" -> 1, "Roland" -> 2)
    */

}

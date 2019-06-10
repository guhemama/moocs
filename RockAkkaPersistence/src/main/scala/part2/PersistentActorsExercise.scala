package part2

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

import scala.collection.mutable

object PersistentActorsExercise extends App {
  /**
    * Persistent actor for a voting station.
    * Persist:
    *  - the citizens who voted
    *  - the pool
    *  The actor must be able to recover its state.
    */
  case class Vote(citizen: String, candidate: String)

  class VotingPool extends PersistentActor with ActorLogging {
    // Mutable state for simplicity
    val citizens: mutable.Set[String] = new mutable.HashSet[String]()
    val pool: mutable.Map[String, Int] = new mutable.HashMap[String, Int]()

    override def persistenceId: String = "votingPool"

    override def receiveCommand: Receive = {
      case vote @ Vote(citizen, candidate) =>
        if (!citizens.contains(vote.citizen)) {
          // This is called Command Sourcing (not Event)
          persist(vote) { _ =>
            log.info(s"Persisted $vote")
            handleVoteStateChange(citizen, candidate)
          }
        } else {
          log.warning(s"Citizen $citizen is trying to vote multiple times")
        }
      case "print" =>
        log.info(s"Current state: \nCitizens: $citizens\nPool: $pool")
    }

    def handleVoteStateChange(citizen: String, candidate: String): Unit = {
      citizens.add(citizen)
      val votes = pool.getOrElse(candidate, 0)
      pool.put(candidate, votes + 1)
    }

    override def receiveRecover: Receive = {
      case vote @ Vote(citizen, candidate) =>
        log.info(s"Recovered: $vote")
        handleVoteStateChange(citizen, candidate)
    }
  }

  val system = ActorSystem("PersistentActorsExercise")
  val votingPool = system.actorOf(Props[VotingPool], "votingPool")

  val votesMap = Map[String, String](
    "Alice" -> "Martin",
    "Bob" -> "Roland",
    "Charlie" -> "Martin",
    "Daniel" -> "Jonas",
    "Sandra" -> "Martin"
  )

  votesMap.keys.foreach { citizen =>
    votingPool ! Vote(citizen, votesMap(citizen))
  }

  votingPool ! "print"
}

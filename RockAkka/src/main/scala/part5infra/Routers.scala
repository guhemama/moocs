package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing._
import com.typesafe.config.ConfigFactory

/**
  * A Router actor routes messages to other actors.
  */
object Routers extends App {
  /**
    * Method #1 - Manual router (no ones uses this).
    */
  class Master extends Actor {
    // Step 1: create routees
    private val slaves = for (i <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave], s"Slave$i")
      context.watch(slave)

      // Creates a Routee from a slave actor
      ActorRefRoutee(slave)
    }

    // Step 2: defines a router
    private val router = Router(RoundRobinRoutingLogic(), slaves)

    override def receive: Receive = {
      // Step 4: handle lifecycle of the routees
      case Terminated(ref) =>
        router.removeRoutee(ref)
        val newSlave = context.actorOf(Props[Slave])
        context.watch(newSlave)
        router.addRoutee(newSlave)
      // Step 3: route the messages
      case message =>
        router.route(message, sender())
    }
  }

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("Routers", ConfigFactory.load().getConfig("routersDemo"))
  val master = system.actorOf(Props[Master])

//  for (i <- 1 to 10) {
//    master ! s"[$i] Hello there!"
//  }

  /**
    * Method #2 - a router actor with its own children pool router.
    * It is the same as before, but it is already implemented.
    */
  val poolMaster = system.actorOf(RoundRobinPool(5).props(Props[Slave]), "poolMaster")
  for (i <- 1 to 10) {
    poolMaster ! s"[$i] Hello there!"
  }

  // Getting pool from config
  val poolMaster2 = system.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")
//  for (i <- 1 to 10) {
//    poolMaster2 ! s"[$i] Hello there!"
//  }

  /**
    * Method #3 - Router with actors created elsewhere (GROUP router).
    * We can also do this from Config.
    */
  val slaveList = (1 to 5)
    .map(i => system.actorOf(Props[Slave], s"Slave$i"))
    .toList

  val slavePaths = slaveList.map(slaveRef => slaveRef.path.toString)
  val groupMaster = system.actorOf(RoundRobinGroup(slavePaths).props())

  for (i <- 1 to 10) {
    groupMaster ! s"[$i] Hello there!"
  }

  /**
    * Special messages:
    *
    * - PoisonPill and Kill are not routed.
    * - AddRoutee, RemoveRoutte, GetRoutee are handled only by the routing actor.
    */
}

package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Timers}

import scala.concurrent.duration._

object TimersSchedulers extends App {
  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SchedulersTimers")
  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("Scheduling reminder for simpleActor")

  // We need an execution context dispatcher to run the scheduler
  // import system.dispatcher
  implicit val executionContext = system.dispatcher

  // Schedule the actor to be executed once with a 1s delay
  system.scheduler.scheduleOnce(1 second) {
    simpleActor ! "Reminder"
  }

  /**
    * Runs every 2s with a 1s initial delay.
    * It returns a Cancellable, which can be cancelled.
    */
  val routine: Cancellable = system.scheduler.schedule(1 second, 2 seconds) {
    simpleActor ! "hearbeat"
  }

  // Cancel it after 5s
  system.scheduler.scheduleOnce(5 seconds) {
    routine.cancel()
  }

  /**
    * Exercise: implement a self-closing actor.
    */
  class SelfClosingActor extends Actor with ActorLogging {
    var schedule: Cancellable = createTimeoutWindow()

    def createTimeoutWindow(): Cancellable = {
      context.system.scheduler.scheduleOnce(1 second) {
        self ! "timeout"
      }
    }

    override def receive: Receive = {
      case "timeout" =>
        log.info("Stopping myself")
        context.stop(self)
      case message =>
        log.info(s"Received $message")
        schedule.cancel()
        schedule = createTimeoutWindow()
    }
  }

  val selfClosingActor = system.actorOf(Props[SelfClosingActor], "SelfClosingActor")
  system.scheduler.scheduleOnce(250 millis) {
    selfClosingActor ! "ping"
  }

  system.scheduler.scheduleOnce(2 seconds) {
    system.log.info("pinging self closing")
    selfClosingActor ! "ping"
  }

  /**
    * Timers are a safer way to schedule messages within an actor.
    */
  case object TimerKey
  case object Start
  case object Reminder
  case object Stop
  class TimerHeartbeatActor extends Actor with ActorLogging with Timers {
    // Each timer has a unique key (TimerKey in this case).
    // Start is the initial message.
    timers.startSingleTimer(TimerKey, Start, 500 millis)

    override def receive: Receive = {
      case Start =>
        log.info("Starting actor")
        // Another timer is created, and the previous one with the same
        // key is removed.
        timers.startPeriodicTimer(TimerKey, Reminder, 1 second)
      case Reminder =>
        log.info("I am alive")
      case Stop =>
        log.warning("Stopping!")
        timers.cancel(TimerKey)
        context.stop(self)
    }
  }

  val timerHeartbeatActor = system.actorOf(Props[TimerHeartbeatActor], "TimerActor")
  system.scheduler.scheduleOnce(5 seconds) {
    timerHeartbeatActor ! Stop
  }
}

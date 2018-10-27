package lectures.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._

object FuturesPromises extends App {
  def calculateMeaningOfLife: Int = {
    Thread.sleep(1000)
    42
  }

  // An implicit ExecutionContext is pased to the future
  val fut = Future {
    calculateMeaningOfLife
  }

  fut onComplete {
    case Success(meaningOfLife) => println(meaningOfLife)
    case Failure(e) => println(e.getMessage)
  }

  Thread.sleep(2000)

  /**
    * A mini social network
    */
  case class Profile(id: String, name: String) {
    def poke(other: Profile): Unit = {
      println(s"${this.name} poked ${other.name}.")
    }
  }

  object SocialNetwork {
    // Our "database"
    val users = Map(
      "fb.id.1" -> "Mark",
      "fb.id.2" -> "Bill",
      "fb.id.3" -> "John"
    )

    val friends = Map(
      "fb.id.1" -> "fb.id.2",
      "fb.id.2" -> "fb.id.3"
    )

    val random = new Random()

    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300)) // Mock a computation
      Profile(id, users(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400)) // Mock a computation
      val bfId = friends(profile.id)
      Profile(bfId, users(bfId))
    }
  }

  // Client: we want mark to poke bill
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // Fallbacks
  val missingProfile = SocialNetwork
    .fetchProfile("fail")
    .recover {
      case e: Throwable => Profile("default", "Default")
    }

  // Fallback to another Future
  val fallbackResult = SocialNetwork
    .fetchProfile("fail")
    .fallbackTo(SocialNetwork.fetchProfile("fb.id.1"))

  // Online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "LOL Bank"

    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(500) // Mock slow operation
      User(name)
    }

    def createTransaction(user: User, merchant: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000) // Mock slow operation
      Transaction(user.name, merchant, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchant: String, cost: Double): String = {
      // Fetch the user
      // Create a transaction
      // Wait for the transaction to finish
      // Return the purchase status
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchant, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds)
    }
  }

  println(BankingApp.purchase("Bob", "iPhone XVI", "Apfel", 1999))

  // Promises - kind of controls futures
  val promise = Promise[Int]()
  val future = promise.future

  // Consumer thread
  future.onComplete {
    case Success(r) => println(s"[consumer] received ${r}")
  }

  // Producer thread
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)

    // This fullfils the promise
    promise.success(42)

    // Or promise.failure to fail the promise

    println("[producer] done")
  })

  producer.start()

  Thread.sleep(1000)
}

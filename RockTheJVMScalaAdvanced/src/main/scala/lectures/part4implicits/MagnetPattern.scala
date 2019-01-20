package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {
  /**
    * Solves the problems created by method overloading.
    *
    * 1. Type erasure
    * 2. Lifting doesn't work for all overloads
    * 3. Code duplication
    * 4. Type inference and default arguments
    */
  class Request
  class Response
  class Serializer[T]

  trait Actor {
    def receive(status: Int): Int
    def receive(request: Request): Int
    def receive(response: Response): Int

    // Context-bound implicit (implicit serializer Serializer[T])
    def receive[T : Serializer](message: T): Int
    def receive[T : Serializer](message: T, status: Int): Int

    // So, lots of overloading on implementations...
  }

  // Refactoring into the pattern
  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromRequest(request: Request) extends MessageMagnet[Int] {
    def apply(): Int = {
      // Logic goes here...
      println("Handling request...")
      42
    }
  }

  implicit class FromResponse(response: Response) extends MessageMagnet[Int] {
    def apply(): Int = {
      // Logic goes here...
      println("Handling response...")
      43
    }
  }

  receive(new Request())
  receive(new Response())

  // 1. No more type erasure problems
  implicit class FromResponseFuture(fut: Future[Response]) extends MessageMagnet[Int] {
    def apply(): Int = 2
  }

  implicit class FromRequestFuture(fut: Future[Request]) extends MessageMagnet[Int] {
    def apply(): Int = 3
  }

  println(receive(Future(new Request)))
  println(receive(Future(new Response)))
}

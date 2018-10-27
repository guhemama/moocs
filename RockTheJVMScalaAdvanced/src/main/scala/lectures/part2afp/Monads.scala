package lectures.part2afp

object Monads extends App {
  // Our version of the Try monad.
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    // Or unit()
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Success[A](value: A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Failure(e: Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  val attempt = Attempt {
    throw new RuntimeException("Oh god")
  }

  println(attempt)

  // Lazy monad
  class Lazy[+A](value: => A) {
    def use: A = value
    def flatMap[B](f: A => Lazy[B]): Lazy[B] = f(value)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }
}

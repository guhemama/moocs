package lectures.part5ts

object HigherKindedTypes extends App {
  // This is a higher kinded type.
  trait AHigherKindedType[F[_]]

  trait Monad[F[_], A] {
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]
  }

  implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    def map[B](f: A => B): List[B] = list.map(f)
  }

  // Multiply works for anything that implements a map/flatMap method.
  def multiply[F[_], A, B](m1: Monad[F, A], m2: Monad[F, B]): F[(A, B)] =
    for {
      a <- m1
      b <- m2
    } yield (a, b)

  val monadList = new MonadList(List(1,2,3))
  monadList.flatMap(x => List(x, x + 1))
  // Monad[List, Int] => List[Int]
  monadList.map(_ * 2)
  // Monad[List, Int] => List[Int]

  println(multiply(List(1,2,3), List("a", "b", "c")))
}

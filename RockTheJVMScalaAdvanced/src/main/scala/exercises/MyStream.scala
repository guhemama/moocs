package exercises

import scala.annotation.tailrec


/**
  * Exercise: implement a lazily evaluated, singly linked stream of elements.
  */
abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // Prepend operator
  def ++[B >: A](that: => MyStream[B]): MyStream[B] // Concatenate two streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // Takes the first n elements

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc
    else tail.toList(head :: acc)
}

object EmptyStream extends MyStream[Nothing] {
  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException
  def tail: MyStream[Nothing] = throw new NoSuchElementException

  def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)
  def ++[B >: Nothing](that: => MyStream[B]): MyStream[B] = that

  def foreach(f: Nothing => Unit): Unit = ()
  def map[B](f: Nothing => B): MyStream[B] = this
  def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  def take(n: Int): MyStream[Nothing] = this
}

// Tail is passed by name because of lazily evaluation.
class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  def isEmpty: Boolean = false

  override val head: A = hd
  override lazy val tail: MyStream[A] = tl // "Call by need"

  def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)

  // Lazy concatenation with call by name => stream
  def ++[B >: A](that: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ that)

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  // Lazy mapping
  def map[B](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f))

  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  def filter(predicate: A => Boolean): MyStream[A] =
    if (predicate(head)) new Cons(head, tail.filter(predicate))
    else tail.filter(predicate)

  def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n - 1))
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = {
    new Cons(start, MyStream.from(generator(start))(generator))
  }
}

object StreamsPlayground extends App {
  // The stream of natural numbers (infinite stream)
  val naturals = MyStream.from(1)(x => x + 1)

  // Lazily evaluated stream of the first 100 naturals (finite stream)
  naturals.take(1000).foreach(println)

  val from0 = 0 #:: naturals
  println(from0.head)

  println(from0.map(_ * 2).take(100).toList())

  println(from0.filter(_ < 10).take(10).toList())

  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] =
    new Cons(first, fibonacci(second, first + second))

  println(fibonacci(1, 1).take(100).toList())
}

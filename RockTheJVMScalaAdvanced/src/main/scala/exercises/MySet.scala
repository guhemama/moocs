package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  /**
    * Exercise implement a functional set.
    */
  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(that: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false
  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
  override def ++(that: MySet[A]): MySet[A] = that
  override def map[B](f: A => B): MySet[B] = new EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  override def filter(predicate: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] =
    if (this.contains(elem)) this
    else new NonEmptySet[A](elem, this)

  override def ++(that: MySet[A]): MySet[A] = tail ++ that + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail.filter(predicate)

    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def createSet(valuesSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if (valuesSeq.isEmpty) acc
      else createSet(valuesSeq.tail, acc + valuesSeq.head)
    }

    createSet(values.toSeq, new EmptySet[A])
  }
}

object MySetTest extends App {
  val s = MySet(1,2,3,4)
  s.foreach(println)
}
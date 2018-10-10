package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(that: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def foreach(f: A => Unit): Unit

  def filter(predicate: A => Boolean): MySet[A]
  def -(elem: A): MySet[A]
  def intersect(that: MySet[A]): MySet[A]
  def diff(that: MySet[A]): MySet[A]

  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false
  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
  override def ++(that: MySet[A]): MySet[A] = that

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  override def foreach(f: A => Unit): Unit = ()

  override def filter(predicate: A => Boolean): MySet[A] = this
  override def -(elem: A): MySet[A] = this
  override def intersect(that: MySet[A]): MySet[A] = this
  override def diff(that: MySet[A]): MySet[A] = this

  // A set that includes everything is the opposite of an empty set.
  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

// This set contains all elements of type A that satisfy a property
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  def contains(elem: A): Boolean = property(elem)

  // In mathematical notation, the new set will be defined as:
  // { x in A | property(x) } + element = { x in A | property(x) || x == element }
  def +(elem: A): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || x == elem)

  // { x in A | property(x) } ++ Set => { x in A | property(x) || Set contains x }
  def ++(that: MySet[A]): MySet[A] =
    new PropertyBasedSet[A](x => property(x) || that(x))

  // We can't map an infinite set.
  def map[B](f: A => B): MySet[B] = fail
  def flatMap[B](f: A => MySet[B]): MySet[B] = fail
  def foreach(f: A => Unit): Unit = fail

  def filter(predicate: A => Boolean): MySet[A] =
    new PropertyBasedSet[A](x => property(x) && predicate(x))

  def -(elem: A): MySet[A] = filter(x => x != elem)
  def intersect(that: MySet[A]): MySet[A] = filter(that)
  def diff(that: MySet[A]): MySet[A] = filter(!that)

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def fail = throw new IllegalArgumentException("Really deep rabbit hole!")
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

  override def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  override def intersect(that: MySet[A]): MySet[A] = filter(that) // expands to that.contains(x)

  override def diff(that: MySet[A]): MySet[A] = filter(!that)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))
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

  val negative = !s // s.unary_! => all the naturals not equal to 1,2,3,4
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(5))
}
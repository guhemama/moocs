package lectures.part1as

object AdvancedPatternMatching extends App {
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"The only element is $head")
    case _ =>
  }

  /**
    * We can decompose regular classes with pattern matching by implementing the unapply method.
    */
  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I am $a yo."
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"Bob's legal status is $status."
  }

  println(legalStatus)

  /**
    * Exercise
    */
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 45
  val mathProp = n match {
    case singleDigit() => "single digit"
    case even() => "even"
    case _ => "odd"
  }

  println(mathProp)

  // Infix patterns can be used with case classes
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescription = either match {
    // same as Or(number, string)
    case number Or string => s"$number is written as $string"
  }
  println(humanDescription)

  // Decomposing sequences
  val varArgs = numbers match {
    case List(1, _*) => "Starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    // Turns a list MyList[A] into an Option containing a sequence Seq[A]
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "wrooong!"
  }

  println(decomposed)
}
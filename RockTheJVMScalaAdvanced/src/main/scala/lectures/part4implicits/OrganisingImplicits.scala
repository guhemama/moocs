package lectures.part4implicits

object OrganisingImplicits extends App {
  println(List(1,4,5,3,2).sorted)

  // scala.Predef has an implicit for int ordering.
  implicit val defaultOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

  case class Person(name: String, age: Int)

  // We can easily define an implicit ordering for any type.
  implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)

  val persons = List(
    Person("Steve", 21),
    Person("Bob", 50),
    Person("Jane", 19)
  )

  println(persons.sorted)

  /**
    * Implicit scope
    * - Local scope
    * - Imported scope
    * - Companions of all types involved in the method signature.
    */
  case class Purchase(units: Int, price: Double)

  val purchases = List(
    Purchase(2, 2.5),
    Purchase(9, 1.1),
    Purchase(1, 7.9)
  )

  // Most used implicit -> companion object
  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => (a.units * a.price) > (b.units * b.price))
  }

  println(purchases.sorted)

  // Less used implicit -> own object
  object UnitCountOrdering {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.units < _.units)
  }

  import UnitCountOrdering._
  println(purchases.sorted)
}

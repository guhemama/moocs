package lectures.part4implicits

object ImplicitsIntro extends App {
  val pair = "Dan" -> "Fancy"

  case class Person(name: String) {
    def greet = s"Hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  // The compiler transforms it into println(fromStringToPerson("Peter"))
  println("Peter".greet)
}

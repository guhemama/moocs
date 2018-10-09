package lectures.part2afp

import scala.io.Source

object PartialFunctions extends App {
  // Creating a partial function with pattern matching.
  val partialFn: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 6 => 99
  }

  println(partialFn(2))
  println(partialFn.isDefinedAt(9)) // false

  // We can turn a partial function into a function with `lift`
  val lifted = partialFn.lift // Int => Option[Int]
  println(lifted(2)) // Some(42)
  println(lifted(5)) // None

  // Partial functions extend normal functions.
  val totalFn: Int => Int = {
    case 1 => 99
  }

  // High-order functions accept partial functions as well.
  val lst = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }

  println(lst)

  // Exercise 1: construct a partial function by instantiating the PartialFunction trait.
  val numbersSayer = new PartialFunction[Int, String] {
    override def isDefinedAt(x: Int): Boolean =
      x == 1 || x == 2 || x == 3

    override def apply(x: Int): String = x match {
      case 1 => "One"
      case 2 => "Two"
      case 3 => "Three"
    }
  }

  println(numbersSayer(1))
  println(numbersSayer.isDefinedAt(5))

  // Exercise 2: create a dumb chatbot as a PF.
  val chatbot: PartialFunction[String, String] = {
    case "Hello" => "Hello, how are you?"
    case "Good, thanks" => "Great!"
    case "What's your name?" => "My name is Bob!"
  }

  Source.stdin.getLines()
    .map(chatbot)
    .foreach(println)
}

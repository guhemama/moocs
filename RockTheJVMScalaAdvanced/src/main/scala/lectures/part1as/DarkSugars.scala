package lectures.part1as

object DarkSugars extends App {
  // Methods with a single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    42
  }

  // Single abstract method pattern
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunkyInstance: Action = (x: Int) => x + 1

  // A Runnable instance is created behind the scenes.
  val aThread = new Thread(() => println("it works!"))

  // :: and #:: are special
  val prependedList = 2 :: List(3, 4)

  // Multi-word method name
  case class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = TeenGirl("Lilly")
  lilly `and then said` "Scala is nuts!"

  // Infix types
  class Composite[A, B]
  val composite: Int Composite String = ??? // Compiler transforms this into Composite[Int, String]

  class -->[A, B]
  val arrow: Int --> String = ??? // Compiler transforms this into -->[Int, String]

  // update() method, like apply, on mutable collections
  val arr = Array(1,2,3)
  arr(2) = 4 // arr.update(2, 4)
}

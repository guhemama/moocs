package lectures.part2afp

object LazyEvaluation extends App {
  // Lazy vals are only evaluated once, and the return value is cached.
  lazy val x: Int = {
    println("Hello")
    42
  }

  println(x) // Hello... 42
  println(x) // 42

  // Lazy vals are not evaluated unless needed, and certain logic expressions
  // are short-circuited.
  def sideEffectCond: Boolean = {
    println("Boo")
    true
  }

  def simpleCond: Boolean = false

  lazy val lazyCond = sideEffectCond
  println(if (simpleCond && lazyCond) "yes" else "no")

  // Implications with call by name functions
  def byNameMethod(n: => Int): Int = {
    // Call by need
    lazy val t = n // The call by name parameter is evaluated three times. If it's lazy, only once.
    t + t + t + 1
  }
  def retrieveMagicValue: Int = {
    // Side effect or long computation
    Thread.sleep(1000)
    println("Waiting...")
    42
  }

  println(byNameMethod(retrieveMagicValue))

  // Filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // List(1,25,5,23)
  val gt20 = lt30.filter(greaterThan20) // List(25,23)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30) // Uses lazy values under the hood
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println(gt20Lazy) // TraversableLike$WithFilter

}

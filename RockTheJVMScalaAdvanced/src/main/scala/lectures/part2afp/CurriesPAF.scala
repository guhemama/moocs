package lectures.part2afp

object CurriesPAF extends App {
  // Curried functions
  val supperAdder: Int => Int => Int =
    x => y => x + y

  val add3 = supperAdder(3)
  println(add3(5))

  // This is a method definition.
  def curriedAdd(x: Int)(y: Int): Int = x + y
  val add4: Int => Int = curriedAdd(4)

  // We can remove the signature by using partial function applications
  val add5 = curriedAdd(5) _

  // Exercise
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int): Int = x + y
  def curriedAddMethod(x: Int)(y: Int): Int = x + y

  val add7a = (x: Int) => simpleAddFunction(x, 7)
  val add7b = (x: Int) => simpleAddMethod(x, 7)
  val add7c = curriedAdd(7) _

  val add7d = simpleAddFunction.curried(7)
  val add7e = simpleAddMethod(7, _: Int)
  val add7f = simpleAddFunction(7, _: Int)

  println(add7a(1))
  println(add7b(1))
  println(add7c(1))
  println(add7d(1))
  println(add7e(1))

  // Underscores are very powerful
  def concat(a: String, b: String, c: String): String = a + b + c
  val insertName = concat("Hello, I'm ", _: String, ", how are you?")
  println(insertName("Bob"))

  // Exercises
  // 1. Process a list of numbers and return their string representation
  // in different formats with a curried format function. Use %4.2f, %8.6f and %14.12f.
  def formatNumber(numberFormat: String)(n: Double): String = numberFormat.format(n)

  val f1 = formatNumber("%4.2f") _
  val f2 = formatNumber("%8.6f") _
  val f3 = formatNumber("%14.12f") _

  println(f1(Math.PI))
  println(f2(Math.PI))
  println(f3(Math.PI))
}

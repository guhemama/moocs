package Progfun

object Lesson21 {
  def main(args: Array[String]) {
    println(sum(id, 3, 5))
    println(sum(cube, 2, 3))

    // Using an anonymous function to double
    println(sum(x => x * x, 1, 2))

    println(sumTail(id, 3, 5))
  }

  /**
    * The type A => B is the type of a function that takes an
    * argument of type A and returns a result of type B.
    */
  def sum(f: Int => Int, a: Int, b: Int): Int =
    if (a > b) 0
    else f(a) + sum(f, a + 1, b)

  /**
    * Tail recursive version of the sum function
    */
  def sumTail(f: Int => Int, a: Int, b: Int): Int = {
    def loop(a: Int, acc: Int): Int = {
      if (a > b) acc
      else loop(a + 1, acc + f(a))
    }
    loop(a, 0)
  }

  // Identify function
  def id(a: Int): Int = a

  def cube(a: Int): Int = a * a * a
}

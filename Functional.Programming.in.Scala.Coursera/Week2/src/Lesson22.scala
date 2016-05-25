/**
  * Created by gustavo on 20/04/16.
  */
object Lesson22 {
  def main(args: Array[String]) {
    println(sum (cube) (1, 2))
    println(fact(3))
  }

  def cube(x: Int): Int = x * x

  /**
    * Currying: sum is a function that returns a new function
    * that takes two Int arguments and returns an Int.
    */
  def sum(f: Int => Int): (Int, Int) => Int = {
    def sumF(a: Int, b: Int): Int =
      if (a > b) 0
      else f(a) + sumF(a + 1, b)
    sumF
  }

  // One line version of the curried version
  def sum2(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 0 else f(a) + sum2(f)(a + 1, b)

  def product(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 1
    else f(a) * product(f)(a + 1, b)

  // def fact(a: Int): Int = product(x => x)(1, a)
  def fact(a: Int): Int = product(identity)(1, a)
}

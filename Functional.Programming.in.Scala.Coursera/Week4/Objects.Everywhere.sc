package idealized.scala

object Main {
  val a: Boolean = True
  val b: Boolean = False

  b < a
  a < b
}

abstract class Boolean {
  def ifThenElse[T](t: T, e: T): T

  def && (x: Boolean): Boolean = ifThenElse(x, False)
  def || (x: Boolean): Boolean = ifThenElse(True, x)
  def unary_! : Boolean        = ifThenElse(False, True)

  def == (x: Boolean): Boolean = ifThenElse(x, x.unary_!)
  def != (x: Boolean): Boolean = ifThenElse(x.unary_!, x)

  /**
    * Provide an implementation of the comparison operator < in class
    * idealized.scala.Boolean.
    * Assume for this that false < true.
    */
  def < (x: Boolean): Boolean  = ifThenElse(False, x)
}

object True extends Boolean {
  def ifThenElse[T](t: T, e: T): T = t
}

object False extends Boolean {
  def ifThenElse[T](t: T, e: T): T = e
}
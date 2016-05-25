package week4

/**
  * Obj with pattern matching
  */
object List {
  // Call with two args
  def apply[T](x1: T, x2: T): List[T] = new Cons(x1, new Cons(x2, new Nil))
  // Call with no args
  def apply[T]() = new Nill
}
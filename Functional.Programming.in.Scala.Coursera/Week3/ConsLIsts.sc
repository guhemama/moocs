singleton[Int](1)
singleton(2) // same thing, no need to add the type
singleton(true)

val list = new Cons(1, new Cons(3, new Cons(5, new Nil)))
println(nth(2, list))



// Function that takes a type parameter
def singleton[T](elem: T) = new Cons[T](elem, new Nil[T])

// Return the nth element of the list
def nth[T](n: Int, list: List[T]): T = {
  if (list.isEmpty) throw new IndexOutOfBoundsException
  else if (n == 0) list.head
  else nth(n - 1, list.tail)
}

// trait that takes a type parameter
trait List[T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
}

// val defines a field of the class at the same time (accessor)
// it's also a valid implementation of the trait functions
class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty = false
}

class Nil[T] extends List[T] {
  def isEmpty = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.tail")
}
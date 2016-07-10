# Week 1

## Queries with for
_for_ expressions can be used like a database query language, like SQL.

* case class Books(title: String, authors: List[String])
* for (b <- books; a <- b.authors if a startsWith "Bird") yield b.title

It works the same like generators in PHP, it only has a different syntax.

Sets are better than lists in the sense that they do not allow duplicates.

The Scala compiler translates for-expressions in terms of map, flatMap and a
lazy variant of filter.

* for (x <- e1) yield e2

Becomes:

*  e1.map(x => e2)


## Monads

Data structures with _map_ and _flatMap_ are called monads.

A monad is a parametric type M[T] with two operations, _flatMap_ and _unit_, that
have to satisfy some laws. In the literature, _flatMap_ is more commonly called _bind_.

* trait M[T] {
    def flatMap[U](f: T => M[U]): M[U]
  }
  def unit[T](x: T): M[T]

_unit_ is different for each monad.

Examples of monads:

* List is monad with unit(x) = List(x)
  Set is a monad with unit(x) = Set(x)
  Option is a monad with unit(x) = Some(x)

To qualify as a monad, a type has to satisfy three laws (both expressions should yield the same result.)

- Associativity
  (m flatMap f) flatMap g = m flatMap (x => (f(x) flatMap g))

- Left unit
  unit(x) flatMap f == f(x)

- Right unit
  m flatMap unit == m

_Try_ resembles an _Option_, but instead of Some/None there is a _Success_ case
and a _Failure_ case that contains an exception. It's used to pass results of
computations that can fail with an exception between threads and computers.

* abstract class Try[+T]
  case class Success[T](x: T)       extends Try[T]
  case class Failure(ex: Exception) extends Try[Nothing]

You can wrap an arbitrary computation in a _Try_.

* import scala.util.{Try, Success, Failure}
  Try(1 + 1)

_Try_ computations can be composed in for-expressions.
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



# Week 2: Lazy Evaluation
One way to define correctness, is by defining a few laws that the program should
abide to.


## Streams
Streams are like lists, but they are tailored to be evaluated on demand.
A Stream can be built by using its factory method:

* Stream(1, 3, 5, 9)

Or by calling _toStream_ on a collection:

* (1 to 1000).toStream

* x #:: xs == Stream.cons(x, xs)


## Lazy Evaluation
If _tail_ is called several times, the performance issue can be avoided by storing
the results of the evaluation of _tail_ and re-using the stored result instead of
computing it again.

* lazy val x = { expr }

Lazy evaluation gives us the opportunity to create things such as infinite streams.



# Week 3: Functions and State
_var_ defines a variable definition.

* var x: String = "abc"
  x = "def"

Object with state can be represented by objects that have some variable elements.

* class BankAccount {
    private var balance = 0

    def deposit(amount: Int): Unit = {
      if (amount > 0) balance = balance + amount
    }

    def withdraw(amount: Int): Int =
      if (0 < amount && amount <= balance) {
        balance = balance - amount
        balance
      } else throw new Error("Insuficient funds")
  }


## Loops
* for (i <- 1 until 3) { ... }
  for (i <- [RANGE]) { ... }

_foreach_ combinator:

* for (i <- 1 until 3; j <- "abc") println(i + " " + j)

Translates to:

* (1 until 3) foreach (i => "abc" foreach (j => println(i + " " + j)))



# Week 4 - Timely Effects

## Imperative Event Handling: The Observer Pattern
* Decouples view from state
* Simple to set up
* Forces imperative style
* Many moving parts that need to be co-ordinated
* Concurrency is hard
* Views are tightly bound to one state


## Functional Reactive Programming
FRP is about reacting to sequences of events that happen in time.

Functional view: aggregate an event sequence into a signal.

_Event-based view:_
  Whenever the mouse moves, an event
    MouseMoved(toPos: Position)
  is fired.

_FRP view:_
  A signal
    mousePosition: Signal[Position]
  at which any point in time represents the current mouse position.

### Fundamental signal operations
1. Obtain the value of the signal at the current time.
2. Define a signal in terms of other signals. (in the lib, use the Signal constructor)
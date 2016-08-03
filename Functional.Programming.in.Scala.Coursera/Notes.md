# Week 1

## Call by name and call by value
* Call by name: its right-hand side is evaluated on each use.
  def foo(x: Int) = x
  def a = 5 + 3

* Call by value: its right-hand side is evaluated at the point of definition itself.
  val x = 2
  val y = square(x) -> y refers to 4, not to square(2)

## Blocks {}
The last expression of a block will be returned.
Definitions inside a block are only visible from within the block.
Definitions inside a block shadow definitions of the same name outside the block.

## Parenthesis
Parenthesis allow code to span multiple lines.



# Week 2

## High order functions
Functions that take other functions as arguments.

_A => B_ is the type of a function that takes an argument of type _A_ and returns
a result of type _B_.

Anonymous functions can be defined as _x => x * x_ (double function).

The type A => B is the type of a function that takes an argument of type A and
returns a result of type B.

 * def sum(f: Int => Int, a: Int, b: Int): Int

## Currying
Currying is the technique of translating the evaluation of a function that takes
multiple arguments (or a tuple of arguments) into evaluating a sequence of
functions, each with a single argument.

Curried sum function: sum is a function that returns a new functio that takes
two Int arguments and returns an Int.

  def sum2(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 0 else f(a) + sum2(f)(a + 1, b)

## Classes
A class introduces a new type.

* class RationalNumber(numer: Int, denom: Int) {
  // Precondition: predefined function that takes a condition and an optional message
  require(denom != 0, "Denominator can't be zero.")

  // A second constructor for the class, takes a single arg
  def this(x: Int) = this(x, 1)

  // Override because this has already been implemented
  override def toString = numerator + "/" + denominator

  // Operation on a unit
  def unary_- = new RationalNumber(-numerator, denominator)
}



# Week 3

## Class hierarchies
A singleton object can be created by using the keyword _object_ instead of _class_.

* object Empty extends IntSet {
    ...
  }

A standalone application must contain an object with a _main_ method.

## Importing packages
* import foo.bar.Baz          // Import a single one from the same package
  import foo.bar.{Baz, Fizz}  // Import two from the same package
  import foo.bar._            // Import everything from the package

## Traits
A class can have multiple supertypes by using traits. Its methods are abstract.

* trait Countable {
    def count: Int
  }

Usage:

* class Basket extends SomeObject with Countable {}

## Class hierarchy
Scala's top class is _scala.Any_.
_scala.AnyVal_ is the superclass for primitives.
_scala.AnyRef_ is the superclass for all objects (it's an alias for java.lang.Object).
_scala.Nothing_ is a subtype of every other type.
_scala.Null_ is a subtype of every class that inherits from _AnyRef_.

## Exception
To throw an exception, use:

* throw new Error("the error message")

## Polymorphism
We can define, for example, lists for any type at the same time, by using Type Parameters.

* def singleton[T](elem: T) = new Cons[T](elem, new Nil[T])
* trait List[T] {
    def isEmpty: Boolean
    def head: T
    def tail: List[T]
  }

These functions and classes take a type parameter T, which may be implicit or explicit.

* singleton[Int](1)
  singleton(2) // same thing, no need to add the type
  singleton(true)

Polymorphism can be achieved by using generics (FP approach) or subtypes (OO approach).



# Week 4

## Functions as objects
In Scala, functions are objects.

## Subtyping and generics
This function takes some type S, which must be a subtype of IntSet, and returns the same type.

* S <: T means S is a subtype of T.
* S >: T means S is a supertype of T, or T is a subtype of S.

 [S :> NonEmpty <: IntSet] restricts the type S to any type between the interval NonEmpty and IntSet.

* def assertAllPos[S <: IntSet](someSet: S): S = ???

## Decomposition
Don't use type casts and type tests - they are "hacky".

## Pattern matching
A _case class_ is similar to a normal class definition, except that it's preceded
by the modifier _case_.

* trait Expr
  case class Number(n: Int) extends Expr
  case class Sum(e1: Expr, e2: Expr) extends Expr

The Scala compiler will automatically and implicitly add companion objects.

* object Number {
    def apply(n: Int) = new Number(n)
  }
  object Sum {
    def apply(e1: Expr, e2: Expr) = new Sum(e1, e2)
  }

So you can write _Number(1)_ instead of _new Number(1)_.

Pattern matching is a generalization of _switch_ from C or Java. It's expressed in
Scala using the keyword _match_.

* def eval(e: Expr): Int = e match {
    case Number(n)   => n
    case Sum(e1, e2) => eval(e1) + eval(e2)
  }

A _MatchError_ exception is thrown if no pattern matches.

## Lists
In Scala, lists are immutable, and recursive, while arrays are flat. All elements
of a list must have the same type.

* val fruits: List[String]  = List("apples", "oranges", "bananas")
  val nums:   List[Int]     = List(1, 2, 3)
  val empty:  List[Nothing] = List()

Scala also has the _cons_ operation, which can be used to build lists.

* fruit = "apples" :: ("oranges" :: ("pears" :: Nil)))
  ints  = 1 :: 2 :: Nil

Lists can be expressed in terms of three operations:

* head
  tail
  isEmpty

It's also possible to decompose lists with pattern matching.

* 1 :: 2 :: xs    Lists that start with 1 and then 2
  x :: Nil        Lists of length 1
  List(x)         Same as x :: Nil
  List()          The empty list, same as Nil

_:::_ concatenates two lists.



# Week 5

* xs.length          The lenght of the list
  xs.last            The last element of the list
  xs.init            A list containing all elements but the last one
  xs take n          A list consisting of the first n elements of xs
  xs drop n          The rest of the collection after taking n elements
  xs(n)              The element of xs at index n
  xs ++ ys           Concatenate two lists
  xs.reverse         The list containing the elements of xs in reversed order
  xs updated (n, x)  Replace the nth element by x
  xs indexOf x       The index of the first element of xs that equals x
  xs contains x      Checks if xs contains x

## High-order list functions

* List.map           Applies an arbitrary operation to all elements of a list
  List(1,2,3).map (x => x*x)
  res4: List[Int] = List(1, 4, 9)

* List.filter        Filters out list elements based on a predicate
  xs filter (x => x > 0)

Functions similar to filter:

* xs filterNot p
  xs partition p
  xs takeWhile p
  xs dropWhile p
  xs span p

## Reduction of lists
Combine a list using a given operator - reduce it.

reduceLeft inserts a binary operator between adjacent elements

* def sum(xs: List[Int]): Int = (0 :: xs) reduceLeft ((x, y) => x + y)
  def sum(xs: List[Int]): Int = (0 :: xs) reduceLeft (_ + _)

foldLeft is like reduceLeft, but it takes an accumulator, z, which is returned
when the function is called on an empty list.

* def sum(xs: List[Int]): Int = (xs foldLeft 0) (_ + _)
  def product(xs: List[Int]): Int = (xs foldLeft 1) (_ * _)

foldLeft and reduceLeft produce trees that lean to the left; foldRight and reduceRight
produce trees that lean to the right.

foldLeft and foldRight are equivalent, although there may be a difference in efficiency
in certain situations.



# Week 6

## Other collections
_Vector_ is an alternative sequence implementation, which are faster to access.
They are implemented as trees.

* val num = Vector(1, -4, 88)
  val ppl = Vector("Bob", "Joe", "Mary")

They support the same operations as lists, with the exception of ::.

x +: xs     Create a new vector with leading element x, followed by all elements of xs.
x :+ xs     Create a new vector with trailing element x, followed by all elements of xs.

_Seq_ is the base class of all sequences. It's a subclass of _Iterable_.

_Array_ and _String_ can also behave as sequences, but they do not inherit from _Seq_,
since they are Java classes. _Arrays_ are mutable; _IndexedSeq_ is their immutable
counterpart.

_Range_ represents a sequence of evenly distributed integers.

* val r: Range = 1 until 5
  val s: Range = 1 to 5
  val t: Range = 1 to 10 by 3

Common sequence operations:

* xs exists p   true if there is an element x of xs such that p(x) holds, false otherwise.
  xs forall p   true if p(x) holds for all elems x of xs, false otherwise.
  xs zip ys     A sequence of pairs drawn from corresponding elems of xs and ys.
                List('a','b') zip List(1,2) = List((a,1), (b,2))
  xs.unzip      Splits a sequence of pairs in two.
                List(('a', 1), ('b', 2)).unzip = List(List('a', 'b'), List(1, 2))
  xs.flatMap f  Applies f to all elements of xs and concatenates the results.
  xs.sum
  xs.product
  xs.max        The maximum of all elems.
  xs.min        The minimum of all elems.
  xs.groupBy f
  xs sortWith f

## For-Expressions
The expression:

* case class Person(age: Int, name: String)
* for (p <- persons if p.age > 20) yield p.name

Is equivalent to:

* persons filter (p => p.age > 20) map (p => p.name)

The for-expression is similar to loops in imperative languages, except that it
builds a list of the results of all iterations. It's form is:

* for ( s ) yield e

Where _s_ is a sequence of generators and filters, and _e_ is an expression
whose value is returned by a iteration.

Find all pairs of positive integers (i, j), such that 1 <= j < i < n, and i + j is prime.

* for {
    i <- 1 until n
    j <- 1 until i
    if isPrime(i + j)
  } yield (i, j)

## Sets
Sets are another abstraction of Scala collections. Most operations on sequences
are also available on sets.

* val fruits = Set("apple", "banana", "passion fruit")
  val nums   = (1 to 6).toSet

Sets are unordered and have no duplicates. Their fundamental operation is _contains_.

* s contains 5

## Maps
A map of type Map[Key, Value] is a data structure that associates keys of type
Key with values of type Value.

* val romanNumerals = Map("I" -> 1, "V" -> 5, 'X' -> 10)

Maps support the same collection operations as other iterables (it extends Iterable[(Key, Value)]).
Maps are also functions:

* romanNumerals("I") // Int = 1
  romanNumerals get "I"  // Option[Int] = Some(1), wraps the response in an Option (Some(x) or None)

_Option_ is defined as a case class, and can be decomposed using pattern matching.
They also support some methods of collections.

* def showCapitalCountry(country: String) = capitalOfCountry.get(country) match {
    case Some(capital) => capital
    case None => "Missing data"
  }

To turn a map into a total function, we can use _withDefaultValue_. It also makes
it not throw exceptions.

* val cap1 = capitalOfCountry withDefaultValue "<unknown>"
  cap1("andorra") // "<unknown>"
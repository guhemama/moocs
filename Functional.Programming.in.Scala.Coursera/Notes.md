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
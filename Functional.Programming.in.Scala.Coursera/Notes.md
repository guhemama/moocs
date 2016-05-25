# Week 1

## Call by name and call by value
* Call by name: its right-hand side is evaluated on each use.
  def foo(x: Int) = x
  def a = 5 + 3

* Call by value: its right-hand side is evaluated at the point of definition itself.
  val x = 2
  val y = square(x) -> y refers to 4, not to square(2)

## Blocks {}
* The last expression of a block will be returned.
* Definitions inside a block are only visible from within the block.
* Definitions inside a block shadow definitions of the same name outside the block.

## Parenthesis
* Parenthesis allow code to span multiple lines.
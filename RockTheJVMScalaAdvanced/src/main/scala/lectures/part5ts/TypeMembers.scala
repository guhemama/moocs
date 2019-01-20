package lectures.part5ts

/**
  * Type members are especially useful when using third-party libraries.
  */
object TypeMembers extends App {
  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // Abstract type member
    type BoundedAnimal <: Animal // Abstract type member upper-bounded in Animal
    type SuperBoundedAnimal >: Dog <: Animal // Lower-bounded in Dog; upper-bounded in Animal
    type Feline = Cat // Type alias
  }

  // Using type aliases
  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  /**
    * Type members can also be used as an alternative to generics.
    */
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class IntList(value: Int) extends MyList {
    override type T = Int
    def add(element: Int): MyList = ???
  }

  // We can also use the type value of objects
  val cat = new Cat
  type CatsType = cat.type

  /**
    * Exercise: enforce a type to be applicable to some types only.
    */
  // This is "locked", written by someone else.
  trait MList {
    type A
    def head: A
    def tail: MList
  }

  trait ApplicableToNumbers {
    type A <: Number
  }

//  This implementation won't compile because of the bounded type on ApplicableToNumbers
//
//  class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
//    type A = String
//    def head = hd
//    def tail = tl
//  }
//
//  class CustomIntList(hd: Int, tl: CustomIntList) extends MList with ApplicableToNumbers {
//    type A = Int
//    def head = hd
//    def tail = tl
//  }
}

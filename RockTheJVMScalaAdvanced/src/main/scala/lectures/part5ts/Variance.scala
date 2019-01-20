package lectures.part5ts

object Variance extends App {
  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Kitty extends Cat
  class Crocodile extends Animal

  /**
    * What is variance? It's an inheritance problem
    * on type substitution of generics.
    */
  // Covariance
  class Cage[T]
  class CovariantCage1[+T]
  val cage1: CovariantCage1[Animal] = new CovariantCage1[Cat]

  // Invariance
  class InvariantCage1[T]
  // val cage2: InvariantCage[Animal] = new InvariantCage[Cat] // won't work

  // Contravariance
  class ContravariantCage1[-T]
  val cage3: ContravariantCage1[Cat] = new ContravariantCage1[Animal]

  class InvariantCage[T](val animal: T)
  class CovariantCage[+T](val animal: T)
  class ContravariantCage[-T](animal: T)

  class MyList[+A] {
    // B is a supertype of A.
    def add[B >: A](element: B): MyList[B] = new MyList[B]
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  /**
    * Invariant, covariant and contravariant exercises
    */
  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  class InvariantParking[T](vehicles: List[T]) {
    def park(vehicle: T): InvariantParking[T] = ???
    def impound(vehicles: List[T]): InvariantParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => InvariantParking[S]): InvariantParking[S] = ???
  }

  class CovariantParking[+T](vehicles: List[T]) {
    def park[S >: T](vehicle: S): CovariantParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CovariantParking[S] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CovariantParking[S]): CovariantParking[S] = ???
  }

  // It's more like a group of actions, and fits the parking place model better.
  class ContravariantParking[-T](vehicles: List[T]) {
    def park(vehicle: T): ContravariantParking[T] = ???
    def impound(vehicles: List[T]): ContravariantParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???

    def flatMap[R <: T, S](f: R => ContravariantParking[S]): ContravariantParking[S] = ???
  }

  /**
    * Rule of thumb:
    * - use covariance when you use it as a collection of things
    * - use contravariance if you use it as a group of actions
    */
}
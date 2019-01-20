package lectures.part5ts

object FBoundedPolymorphism extends App {

  // This is a recursive type, also known as F-Bounded Polymorphism
//  trait Animal[A <: Animal[A]] {
//    def breed: List[Animal[A]] // The return type must match the recursive type.
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List[Cat] = ??? // List[Cat]
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List[Dog] = ??? // List[Dog]
//  }

  // E.g. an ORM trait:
//  trait Entity[E <: Entity[E]]

  // F-Bounded Polymorphism and Self-Types can improve the type constraints.
  trait Animal2[A <: Animal2[A]] { self: A =>
    def breed: List[Animal2[A]]
  }

  class Cat2 extends Animal2[Cat2] {
    override def breed: List[Animal2[Cat2]] = ???
  }

  // This won't work.
  // class Crocodile extends Animal2[Dog2]

  // Another solution with type classes.
  trait Animal3
  trait CanBreed[A] {
    def breed(a: A): List[A]
  }

  class Dog extends Animal3
  object Dog {
    implicit object DogsCanBreed extends CanBreed[Dog] {
      def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class CanBreedOps[A](animal:A ) {
    def breed(implicit canBreed: CanBreed[A]): List[A] =
      canBreed.breed(animal)
  }

  val dog = new Dog
  dog.breed
}

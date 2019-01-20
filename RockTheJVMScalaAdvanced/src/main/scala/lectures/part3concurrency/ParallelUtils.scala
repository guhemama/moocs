package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {
  /**
    * 1 - Parallel collections
    * There are parallel versions of Seq, Vector, Array, Map, HashMap, Set, and so on.
    */
  val parList = List(1,2,3).par
  val parVector = ParVector[Int](1,2,3)

  /**
    * Measure the type of an operation
    * @param operation
    * @tparam T
    * @return
    */
  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  val parallelTime = measure {
    list.par.map(_ + 1)
  }

  println(s"Serial time $serialTime\nParallel time $parallelTime")

  /**
    * Map, flatMap, filter, foreach are safe.
    * reduce, fold may return different results on parallel collections.
    */
  println(List(1,2,3).reduce(_ - _))
  println(List(1,2,3).par.reduce(_ - _))

  /**
    * Controlling parallel collections.
    * We can control them via tasksupport.
    */
  parVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))

  /**
    * 2. Atomic ops: thread-safe operations.
    */
  val atomic = new AtomicReference[Int](2)
  val currentValue = atomic.get() // Thread-safe read
  atomic.set(4) // Thread-safe write

}

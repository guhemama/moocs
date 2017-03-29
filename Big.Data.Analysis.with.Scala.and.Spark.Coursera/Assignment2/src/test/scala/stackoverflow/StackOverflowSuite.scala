package stackoverflow

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.io.File

@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll {

  val postings = List(
      Posting(1, 1, Some(3), None, 0,  Some("PHP")),
      Posting(1, 2, None, None,    0,  Some("Ruby")),
      Posting(2, 3, None, Some(1), 2,  None),
      Posting(2, 4, None, Some(1), 5,  None),
      Posting(2, 5, None, Some(2), 12, None),
      Posting(1, 6, None, None,    0,  Some("Scala")),
      Posting(2, 7, None, Some(6), 0,  None)
    )

  lazy val testObject = new StackOverflow {
    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }

  test("groupedPosting groups questions and answers") {
    val rdd = StackOverflow.sc.makeRDD(postings)
    val results = testObject.groupedPostings(rdd).collect()

    assert(results.size === 3)
    assert(results.contains(
      (2, Iterable(
        (Posting(1, 2, None, None, 0, Some("Ruby")), Posting(2, 5, None, Some(2), 12, None))
      ))
    ))
    assert(results.contains(
      (1, Iterable(
        (Posting(1, 1, Some(3), None, 0, Some("PHP")), Posting(2, 3, None, Some(1), 2,  None)),
        (Posting(1, 1, Some(3), None, 0, Some("PHP")), Posting(2, 4, None, Some(1), 5,  None))
      ))
    ))
  }

  test("scoredPostings finds the correct max scores groups questions and answers") {
    val rdd = StackOverflow.sc.makeRDD(postings)
    val groupedPostings = testObject.groupedPostings(rdd)
    val results = testObject.scoredPostings(groupedPostings).collect()

    assert(results.size === 3)
    assert(results.contains((Posting(1, 1, Some(3), None, 0, Some("PHP")), 5)))
    assert(results.contains((Posting(1, 2, None, None, 0, Some("Ruby")), 12)))
    assert(results.contains((Posting(1, 6, None, None, 0, Some("Scala")), 0)))
  }

  test("vectorPostings calculates the vectors of the kmeans correctly") {
    val rdd = StackOverflow.sc.makeRDD(postings)
    val groupedPostings = testObject.groupedPostings(rdd)
    val scoredPostings  = testObject.scoredPostings(groupedPostings)
    val results = testObject.vectorPostings(scoredPostings).collect()

    assert(results.size === 3)
    assert(results.contains((100000, 5))); // PHP
    assert(results.contains((300000, 12))); // Ruby
    assert(results.contains((500000, 0))); // Scala
  }
}
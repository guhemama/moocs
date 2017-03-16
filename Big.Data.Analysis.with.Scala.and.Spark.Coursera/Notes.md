# Week 1 - From Parallel to Distributed

Distribution introduces important concerns beyond what we had to worry about when dealing with parallelism in the shared memory case:

* _Partial failure_: crash failures are a subset of the machines involved in a distributed computation.
* _Latency_: certain operations have a much higher latency than other operations due to network communication.

## Basics of Spark's RDDs

Resilient Distributed Datasets (RDDs) are Scala's distributed collections. They seem a lot like **immutable** sequential or parallel Scala collections. Most operations on RDDs, like Scala's immutable `List`, are high-order functions.

RDDs can be created in two ways: by transforming an existing RDD (e.g. through a higher-order function that returns a new RDD) or from a `SparkSession` object. `SparkSession` defines some useful methods that can be used to created an populate a new RDD, such as `parallelize` (it converts a local collection to a RDD) and `textFile` (it reads a text file and returns a RDD of `String`).
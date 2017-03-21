# Week 1 - From Parallel to Distributed

Distribution introduces important concerns beyond what we had to worry about when dealing with parallelism in the shared memory case:

- _Partial failure_: crash failures are a subset of the machines involved in a distributed computation.
- _Latency_: certain operations have a much higher latency than other operations due to network communication.

## Basics of Spark's RDDs

Resilient Distributed Datasets (RDDs) are Scala's distributed collections. They seem a lot like **immutable** sequential or parallel Scala collections. Most operations on RDDs, like Scala's immutable `List`, are high-order functions.

RDDs can be created in two ways: by transforming an existing RDD (e.g. through a higher-order function that returns a new RDD) or from a `SparkSession` object. `SparkSession` defines some useful methods that can be used to created an populate a new RDD, such as `parallelize` (it converts a local collection to a RDD) and `textFile` (it reads a text file and returns a RDD of `String`).

## Transformations and actions

- _Transformations_: returns new RDDs as results. They are lazy.
- _Actions_: computes a result based on a RDD, and either returned or saved to an external storage system. They are eager (not lazy).

```scala
val largeList: List[String] = ...
val wordsRdd: RDD[String] = sc.parallelize(largeList) // val sc: SparkContext
val lengthsRdd = wordsRdd.map(_.length) // This is a transformation
val totalChars = lengthsRdd.reduce(_ + _) // This is an action, and returns an integer
```

### Common transformations

- `map`: applies a function to each element and returns an RDD of the results.
- `flatMap`: applies a function to each element and returns an RDD of the contents of the iterators returned.
- `filter`: filters the list based on a predicate.
- `distinct`: returns an RDD with duplicates removed.

### Common actions

- `collect`: returns all elements from the RDD.
- `count`: returns the number of elements of the RDD.
- `take`: returns the first _n_ elements of the RDD.
- `reduce`: combines the elements in the RDD together using a operation and returns the result.
- `foreach`: apply a function to each element in the RDD.

### Transformations on two RDDs

RDDs also support set-like transformations:

- `union`
- `intersection`
- `subtract`
- `cartesian`

## Caching and persistence

Spark allows us to control what is cached in memory.

```scala
val lastYearLogs: RDD[String] = ...
val logsWithErrors = lastYearLogs.filter(_.contains("ERROR")).persist()
val logsWithErrors = lastYearLogs.filter(_.contains("ERROR")).cache()
// caches logsWithErrors, so it's evaluated/calculated only once
```

## How Spark jobs are executed

Execution of a Spark program:

1. The driver program runs the Spark application, which creates a SparkContext upon start-up.
2. The SparkContext connects to a cluster manager (e.g., Mesos/YARN) which allocates resources.
3. Spark acquires executors on nodes in the cluster, which are processes that run computations and store data for your application.
4. Next, driver program sends your application code to the executors.
5. Finally, SparkContext sends tasks for the executors to run.



# Week 2 - Reduction Operations and Distributed Key-Value Pairs

**Reduction operations** walk through a collection and combine neighbouring elements of the collection together to produce a single combined result.

`fold` enables us to parallelize things, but it restricts us to always returning the same type.

```scala
def fold(z: A)(f: (A, A) => A): A
```

We also can use the `reduce` and `aggregate` on Spark collections. `aggregate` is a more desirable reduction operator a majority of the time, because it allows us to _project down from larger/more complex data types_.

## Pair RDDs

Often, when working with distributed data, it's useful to organize data into **key-value pairs**. In Spark, those are called **Pair RDDs**, and they are especially useful because they allow you to act on each key in parallel or regroup data across the network.

When a RDD is created with a pair as its element type, Spark automatically ads a number of useful methods for pairs:

```scala
def groupByKey(): RDD[(K, Iterable[V])]
def reduceByKey(func: (V, V) => V): RDD[(K, V)]
def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
```

Pair RDDs are most often created from already existing non-pair RDDs; for exemple, by using the `map` operation on RDDs.

## Transformations and actions on Pair RDDs

Important transformations defined on Pair RDDs (but not on regular RDDs):

```scala
// Breaks up a collection into two or more collections, grouping all values of
// a pair that have the same key.
def groupByKey(): RDD[(K, Iterable[V])]

// Reduces the values of a key - it's like running groupByKey and then doing a
// reduce operation
def reduceByKey(func: (V, V) => V): RDD[(K, V)]

// It applies a function to only the vaues of a Pair RDD
def mapValues[U](f: V => U): RDD[(K, U)]

// Returns a RDD with the keys of each tuple
def keys: RDD[K]

def join
def leftOuterJoin
def rightOuterJoin
```

Important action defined on Pair RDDs (but not on regular RDDs):

```scala
// It counts the number of elements per key in a Pair RDD, returning a normal
// Scala Map mapping keys to counts.
def countByKey(): Map[K, Long]
```

## Joins

Joins are used to combine multiple RDDs.

### Inner joins

Inner joins return a new RDD containing combined pairs **whose keys are present in both input RDDs**.

```scala
def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
```

### Outer joins

Outer joins return a new RDD containing combined pairs **whose keys don't have to be present in both input RDDs**. They are particularly useful for customizing how the resulting joined RDD deals with missing keysl

```scala
def leftOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (V, Option[W]))]
def rightOuterJoin[W](other: RDD[(K, W)]): RDD[(K, (Option[V], W))]
```
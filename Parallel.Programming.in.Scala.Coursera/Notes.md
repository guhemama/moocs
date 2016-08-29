# Week 1 - Parallel Programming

## Introduction to Parallel Programming
What is parallel computing? It's a type of computation in which many calculations
are performed at the same time.

* Basic principle: computation can be divided into smaller subproblems, each of
which can be solved simultaneously.
* Assumption: we have parallel hardware at our disposal, which is capable of
executing these instructions in paralell.

### Parallel x Concurrent
* Parallel program: uses parallel hardware to execute computation more quickly.
Efficiency is its main concern.
* Concurrent program: may or may not execute multiple executions at the same time.
Improved modularity, responsiveness or maintainability.

### Paralellism Granularity
Parallelism manisfests itself at different granularity levels.
* Bit-level parallelism: processing multiple bits of data in parallel.
* Instruction-level parallelism: executing different instructions from the same
instruction stream in parallel.
* Task-level parallelism: executing separate instruction streams in parallel.


## Running computations in parallel
To run computations in parallel, just use the _parallel_ combinator:

* val (sum1, sum2) = parallel(compute1(a, b), compute2(b, c))

In this case, two threads will be used. Recursion allows us to use an indeterminate
number of threads.

## First class tasks
We can use the construct _task_ to to start computations "in the background".
Calling _t.join_ blocks and awaits for the computation result.

val t1 = task(e1)
val t2 = task(e2)
val v1 = t1.join
val v2 = t2.join


## Benchmarking
ScalaMeter can be used to benchmark Scala programs.

* val time = measure {
    (0 until 10000).toArray
  }

  > time: Double = 21.490

It returns the running time in milliseconds.

Using a warmer, we can guarantee a steady state is reached before collecting
results, i.e., the JVM is warm and optimized.

* val time = withWarmer(new Warmer.Default) measure {
    (0 until 10000).toArray
  }

  > time: Double = 4.059

ScalaMeter can also benchmark memory usage, CPU usage, and many other things.

## How fast are parallel programs?
Asymptotic analysis is important to understand how algorithms scale.
Recursive computations can be analyzed as trees.



# Week 2 - Basic Task Parallel Algorithms

## Parallel sorting

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


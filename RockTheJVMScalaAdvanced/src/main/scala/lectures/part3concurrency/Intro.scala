package lectures.part3concurrency

object Intro extends App {
  // JVM threads
  val t = new Thread( new Runnable {
    override def run(): Unit = println("Running in parallel")
  })

  // Creates a JVM thread, that runs on the OS thread.
  t.start()

  // Block until t finishes running
  t.join()
}

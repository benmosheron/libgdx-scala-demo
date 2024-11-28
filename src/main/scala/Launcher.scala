package com.benmosheron

class Launcher {
  private var done: Boolean = false

  private val finish: () => Unit = () =>
    synchronized {
      done = true
      notifyAll()
    }

  def runUntilFinished(f: (() => Unit) => Unit): Unit = synchronized {
    f(finish)
    while (!done) wait()
  }
}

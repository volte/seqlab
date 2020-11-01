package seqlab.core.timeline

import seqlab.core.TimeScale

import scala.concurrent.duration.{DurationInt, DurationLong, FiniteDuration}

/**
  * A timeline runner is responsible for running a timeline in real time.
  */
class TimelineRunner(val cursor: Timeline#Cursor, options: TimelineRunner.Options = TimelineRunner.Options()) {
  private var thread: Option[Thread] = None

  var ticksPerSecond: Long = options.timeScale.ticksPerSecond
  def tickLength: FiniteDuration = 1.second / ticksPerSecond

  private object Worker extends Runnable {
    override def run(): Unit = {
      var lastTime = System.nanoTime()
      var deltaTicks = 0.0
      var done = false
      while (!done) {
        val burstStartTime = System.nanoTime()
        var currentTime = lastTime
        while (currentTime < burstStartTime + options.burstLength.toNanos) {
          currentTime = System.nanoTime()
          val delta = currentTime - lastTime
          deltaTicks += (delta.nanos / tickLength)
          if (deltaTicks >= 1) {
            val remainder = deltaTicks % 1
            done |= !cursor.advance(deltaTicks.toInt)
            deltaTicks = remainder
          }
          lastTime = currentTime
        }
        if (Thread.currentThread().isInterrupted) {
          done = true
        }
        Thread.`yield`()
      }
    }
  }

  def start(): Unit = {
    val newThread = new Thread(Worker)
    newThread.start()
    thread = Some(newThread)
  }

  def stop(): Unit = {
    thread.foreach(_.interrupt())
  }

  def join(): Unit = {
    thread.foreach(_.join())
  }
}

object TimelineRunner {
  case class Options(burstLength: FiniteDuration = 100.millis, timeScale: TimeScale = TimeScale(1.second.toNanos))
}

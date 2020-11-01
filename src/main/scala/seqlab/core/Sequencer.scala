package seqlab.core

import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.duration.{DurationInt, DurationLong, FiniteDuration}

/**
  * A sequencer is responsible for executing a schedule in real time.
  */
class Sequencer[E](val schedule: ScheduledQueue[E], options: Sequencer.Options = Sequencer.Options()) {
  private var thread: Option[Thread] = None

  var ticksPerSecond: Long = options.ticksPerSecond
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
            schedule.dequeue(deltaTicks.toInt)
            done = schedule.isEmpty
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

object Sequencer {
  case class Options(burstLength: FiniteDuration = 100.millis, ticksPerSecond: Long = 1.second.toNanos)
}

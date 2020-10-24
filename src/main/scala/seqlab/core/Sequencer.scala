package seqlab.core

import java.util.concurrent.Flow.Publisher
import java.util.concurrent._

import akka.actor.{ActorSystem, Cancellable}

import scala.collection.mutable
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.duration.{Duration, DurationInt, DurationLong}

/**
 * Runs scheduled events in sequence according to a regular clock.
 */
class Sequencer[T](options: Sequencer.Options) {
  private implicit val actorSystem: ActorSystem = options.actorSystem
  private val tickPeriod = 1.seconds.toNanos / options.tickFrequency
  private val events = new mutable.PriorityQueue[ScheduledEvent[T]]()

  private var timeOffset: Long = System.nanoTime()
  private var time: Long = 0
  private var cancellable: Cancellable = Cancellable.alreadyCancelled

  private class Worker(queue: SourceQueueWithComplete[T]) extends Runnable {
    def run(): Unit = {
      val deltaTime = (System.nanoTime() - timeOffset) - time
      val nextTime = time + deltaTime

      while (events.nonEmpty && events.head.time < nextTime) {
        val nextEvent = events.dequeue()
        queue.offer(nextEvent.data)
      }

      time = nextTime
    }
  }

  /**
   * Start the sequencer, initializing the work thread. Returns a Flow publisher that emits events as they occur.
   */
  def start(): Publisher[T] = {
    import actorSystem.dispatcher

    timeOffset = System.nanoTime()
    time = 0
    val (queue, publisher) = Source
      .queue[T](options.bufferSize, OverflowStrategy.dropTail)
      .toMat(JavaFlowSupport.Sink.asPublisher(false))(Keep.both)
      .run()

    val worker = new Worker(queue)
    cancellable = actorSystem.scheduler.scheduleAtFixedRate(0.nanoseconds, tickPeriod.nanoseconds)(worker)
    publisher
  }

  /**
   * Shut down the sequencer, terminating the work thread.
   */
  def stop(): Unit = {
    cancellable.cancel()
  }

  /**
   * Add the given events to the sequencer's list of events
   */
  def schedule(newEvents: Seq[ScheduledEvent[T]]): Unit = {
    events.enqueue(newEvents.filter(x => x.time >= time): _*)
  }
}

object Sequencer {
  trait Options {
    val actorSystem: ActorSystem
    val tickFrequency: Long = 1000
    val bufferSize: Int = 1024
  }
}

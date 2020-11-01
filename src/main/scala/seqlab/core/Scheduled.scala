package seqlab.core

import scala.concurrent.duration.FiniteDuration

/**
  * Associates a piece of data with a given time in ticks.
  */
case class Scheduled[+T](time: TimePoint, data: T) extends ScheduledOps[T]

object Scheduled {
  implicit def ordering[T]: Ordering[Scheduled[T]] =
    (x: Scheduled[T], y: Scheduled[T]) => x.time.compare(y.time)

  implicit def fromTuple[T](input: (TimePoint, T)): Scheduled[T] =
    Scheduled[T](input._1, input._2)

  implicit class LongOperator(time: TimePoint) {
    def -->:[T](data: T): Scheduled[T] = Scheduled(time, data)
  }
}

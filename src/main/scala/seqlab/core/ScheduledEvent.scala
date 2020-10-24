package seqlab.core

/**
 * Associates a piece of data with a given time in ticks.
 */
case class ScheduledEvent[+T](time: Long, data: T)

object ScheduledEvent {
  implicit def ordering[T]: Ordering[ScheduledEvent[T]] = (x: ScheduledEvent[T], y: ScheduledEvent[T]) => -x.time.compare(y.time)
}
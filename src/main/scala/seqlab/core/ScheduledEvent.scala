package seqlab.core

/**
  * Associates a piece of data with a given time in ticks.
  */
trait ScheduledEvent[+T] extends ScheduledEventOps[T] {

  /**
    * The time at which this event is scheduled.
    */
  def time: Long

  /**
    * The data associated with this event.
    */
  def data: T
}

object ScheduledEvent {
  implicit def ordering[T]: Ordering[ScheduledEvent[T]] =
    (x: ScheduledEvent[T], y: ScheduledEvent[T]) => x.time.compare(y.time)

  def apply[T](time0: Long, data0: T): ScheduledEvent[T] =
    new ScheduledEvent[T] {
      override def time: Long = time0
      override def data: T = data0

      override def toString: String = s"$time: $data"
    }
}

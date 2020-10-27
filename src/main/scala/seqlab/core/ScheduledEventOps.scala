package seqlab.core

/**
  * Extended ops on `ScheduledEvent`.
  */
trait ScheduledEventOps[+T] { this: ScheduledEvent[T] =>

  /**
    * Map a function over the time of a scheduled event.
    */
  def mapTime(fn: Long => Long): ScheduledEvent[T] =
    ScheduledEvent(fn(time), data)

  /**
    * Map a function over the data of a scheduled event.
    */
  def map[U](fn: T => U): ScheduledEvent[U] =
    ScheduledEvent(time, fn(data))
}

object ScheduledEventOps {
  implicit class ArrowOperator[T](data: T) {
    def -->:(time: Long): ScheduledEvent[T] = ScheduledEvent(time, data)
  }
}

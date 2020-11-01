package seqlab.core

/**
  * Extended ops on `ScheduledEvent`.
  */
trait ScheduledOps[+T] { this: Scheduled[T] =>

  /**
    * Map a function over the time of a scheduled event.
    */
  def mapTime(fn: TimePoint => TimePoint): Scheduled[T] =
    Scheduled(fn(time), data)

  /**
    * Map a function over the data of a scheduled event.
    */
  def map[U](fn: T => U): Scheduled[U] =
    Scheduled(time, fn(data))
}

object ScheduledOps {
  implicit class ArrowOperator[T](data: T) {
    def -->:(time: Long): Scheduled[T] = Scheduled(time, data)
  }
}

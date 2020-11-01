package seqlab.core

/** A queue for scheduled events that keeps track of the current time. */
class ScheduledQueue[E](initialEvents: Scheduled[E]*) {
  private var _time: TimePoint = 0
  private var events = Seq(initialEvents.sorted: _*)

  /** Pop the events within the given timespan of the front of the queue */
  def dequeue(span: TimeSpan): Seq[Scheduled[E]] = {
    _time += span
    if (events.isEmpty) {
      return Seq()
    }
    val (result, keep) = events.span(_.time < _time)
    events = keep
    result
  }

  /** Push the specified events to this queue. */
  def enqueue(newEvents: Seq[Scheduled[E]]): Unit = {
    events = events.concat(newEvents).sorted
  }

  /** The current time of this queue. */
  def time: TimePoint = _time

  /** The total length of time of this schedule. This is equal to the scheduled time of the final instruction. */
  def length: TimeSpan =
    events match {
      case Seq() => 0
      case xs    => xs.last.time.ticks
    }

  /** Returns true if the event queue is empty. */
  def isEmpty: Boolean = events.isEmpty
}

object ScheduledQueue {
  def parallel[E](schedules: ScheduledQueue[E]*): ScheduledQueue[E] =
    new ScheduledQueue(schedules.flatMap(_.events).sorted: _*)

  def sequence[E](schedules: ScheduledQueue[E]*): ScheduledQueue[E] =
    new ScheduledQueue(
      schedules
        .foldLeft((TimePoint(0), Seq[Scheduled[E]]())) {
          case ((time, events), schedule) => {
            (time + schedule.length, events ++ schedule.events.map(_.mapTime { _ + time.ticks }))
          }
        }
        ._2: _*
    )
}

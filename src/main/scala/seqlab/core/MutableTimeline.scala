package seqlab.core

import scala.collection.mutable

trait MutableTimeline[E] extends Timeline[E] {
  def add(event: ScheduledEvent[E]*): Unit
}

object MutableTimeline {
  def apply[E](initialEvents: ScheduledEvent[E]*): MutableTimeline[E] =
    new MutableTimeline[E] {
      private var _time: Long = 0
      private var events = Seq(initialEvents.sorted: _*)

      override def advance(span: TimeSpan): Seq[ScheduledEvent[E]] = {
        _time += span.ticks
        val (result, keep) = events.span(_.time < _time)
        events = keep
        result
      }

      override def time: Long = _time
      override def done: Boolean = events.isEmpty
      override def add(newEvents: ScheduledEvent[E]*): Unit =
        events = events.concat(newEvents).sorted
    }
}

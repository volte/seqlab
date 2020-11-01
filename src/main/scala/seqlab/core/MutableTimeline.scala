package seqlab.core

import scala.collection.mutable

trait MutableTimeline[E] extends Timeline[E] {
  def add(relative: Boolean)(event: ScheduledEvent[E]*): Unit
}

object MutableTimeline {
  def apply[E](initialEvents: ScheduledEvent[E]*): MutableTimeline[E] =
    new MutableTimeline[E] {
      private var _time: Long = 0
      private var _done: Boolean = false
      private var events = Seq(initialEvents.sorted: _*)

      override def advance(span: TimeSpan): Seq[ScheduledEvent[E]] = {
        if (_done) {
          return Seq()
        }
        _time += span.ticks
        val (result, keep) = events.span(_.time < _time)
        events = keep
        _done = events.isEmpty
        result
      }

      override def time: Long = _time
      override def done: Boolean = _done

      override def break(): Unit =
        _done = true

      override def add(relative: Boolean)(newEvents: ScheduledEvent[E]*): Unit = {
        events = if (relative) {
          events.concat(newEvents.map(_.mapTime(_ + _time))).sorted
        } else {
          events.concat(newEvents).sorted
        }
      }
    }
}

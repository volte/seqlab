package seqlab.core

import scala.collection.immutable.Queue

/**
  * A sequencer combines multiple timelines in sequence.
  */
class Sequencer(initialTimelines: Timeline*) extends Timeline {
  class Cursor extends Timeline.Cursor {
    private var cursors: Queue[Timeline#Cursor] = Queue(initialTimelines.map(_.instantiate()): _*)
    private var _time: TimePoint = 0

    /** Advance the timeline by the given timespan. Returns false when the timeline has completed. */
    override def advance(span: TimeSpan): Boolean = {
      if (cursors.isEmpty) {
        return false
      }
      var remaining = span
      while (cursors.nonEmpty && remaining > 0) {
        val cursor = cursors.head
        val prevTime = cursor.time
        val curDone = cursor.advance(remaining)
        val nextTime = cursor.time
        val delta = nextTime - prevTime
        remaining -= delta

        if (curDone) {
          val (_, rest) = cursors.dequeue
          cursors = rest
        }
      }
      cursors.nonEmpty
    }

    /** Returns true if the timeline is finished executing. */
    override def done: Boolean =
      cursors.isEmpty

    /** Abort the timeline. The `done` method should return true immediately after calling this. */
    override def abort(): Unit =
      cursors = Queue()

    /** Append a timeline to this mixer. */
    def append(timeline: Timeline): Unit =
      cursors = cursors.enqueue(timeline.instantiate())

    /** The time relative to the start of the timeline pointed to by this cursor. */
    override def time: TimePoint =
      _time
  }
}

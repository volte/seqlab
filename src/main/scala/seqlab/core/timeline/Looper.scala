package seqlab.core.timeline
import seqlab.core.{TimePoint, TimeSpan}

/**
  * A looper repeats a timeline indefinitely.
  */
class Looper(timeline: Timeline) extends Timeline {
  class LooperCursor extends Cursor {
    private var cursor = timeline.create()
    private var _time: TimePoint = 0
    private var aborted: Boolean = false

    /** Advance the timeline by the given timespan. Returns false when the timeline has completed. */
    override def advance(span: TimeSpan): Boolean = {
      var remaining = span
      while (remaining > 0) {
        val prevTime = cursor.time
        val curDone = cursor.advance(remaining)
        val nextTime = cursor.time
        val delta = nextTime - prevTime
        remaining -= delta
        if (curDone) {
          cursor = timeline.create()
        }
      }
      true
    }

    /** Returns true if the cursor is at the end of the timeline (the `advance` method has turned false). */
    override def done: Boolean =
      aborted

    /** Abort the timeline instance. The `done` method should return true immediately after calling this. */
    override def abort(): Unit =
      aborted = true

    /** The time relative to the start of the timeline pointed to by this cursor. */
    override def time: TimePoint =
      _time
  }

  /** Create a new instance of the timeline and return the cursor to its beginning. */
  override def create(): Cursor =
    new LooperCursor()
}

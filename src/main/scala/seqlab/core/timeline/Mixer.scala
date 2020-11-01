package seqlab.core.timeline

import seqlab.core.{TimePoint, TimeSpan}

/**
  * A mixer combines multiple timelines in parallel.
  */
class Mixer(initialTimelines: Timeline*) extends Timeline {
  private class MixerCursor extends Cursor {
    private var cursors: Seq[Timeline#Cursor] = initialTimelines.map(_.create())
    private var _time: TimePoint = 0

    /** Advance the timeline by the given timespan. Returns false when the timeline has completed. */
    override def advance(span: TimeSpan): Boolean = {
      var curDone = true
      var maxDelta: TimeSpan = 0
      for (cursor <- cursors) {
        val prevTime = cursor.time
        curDone &= cursor.advance(span)
        val nextTime = cursor.time
        val delta = nextTime - prevTime
        if (delta > maxDelta) {
          maxDelta = delta
        }
      }
      _time += maxDelta
      cursors = cursors.filterNot(_.done)
      cursors.nonEmpty
    }

    /** Abort the timeline. The `done` method should return true immediately after calling this. */
    override def abort(): Unit =
      cursors = Seq()

    /** Add a timeline to this mixer at the cursor position. */
    def start(timeline: Timeline): Unit = {
      cursors :+= timeline.create()
    }

    /** Returns true if the cursor is at the end of the timeline (the `advance` method has turned false). */
    override def done: Boolean = cursors.isEmpty

    /** The time relative to the start of the timeline pointed to by this cursor. */
    override def time: TimePoint = _time
  }

  /** Create a new instance of the timeline and return the cursor to its beginning. */
  override def create(): Cursor = new MixerCursor()
}

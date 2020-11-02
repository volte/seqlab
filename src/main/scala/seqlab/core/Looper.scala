package seqlab.core

/**
  * A looper repeats a timeline indefinitely.
  */
class Looper(timeline: Timeline) extends Timeline {
  class Cursor extends Timeline.Cursor {
    private var cursor = timeline.instantiate()
    private var _time: TimePoint = 0
    private var aborted: Boolean = false

    override def advance(span: TimeSpan): Boolean = {
      var remaining = span
      while (remaining > 0) {
        val prevTime = cursor.time
        val curDone = !cursor.advance(remaining)
        val nextTime = cursor.time
        val delta = nextTime - prevTime
        _time += delta
        remaining -= delta
        if (curDone) {
          cursor = timeline.instantiate()
        }
      }
      true
    }

    override def done: Boolean =
      aborted

    override def abort(): Unit =
      aborted = true

    override def time: TimePoint =
      _time
  }

  override def instantiate(): Cursor =
    new Cursor()
}

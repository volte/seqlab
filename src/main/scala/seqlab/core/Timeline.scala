package seqlab.core

/**
  * A timeline represents behaviour that executes over time.
  */
trait Timeline {
  type Cursor <: Timeline.Cursor

  /** Create a new instance of the timeline and return the cursor to its beginning. */
  def instantiate(): Cursor
}

object Timeline {

  /**
    * A timeline cursor represents the current execution state of a timeline.
    */
  trait Cursor { this: AnyRef =>

    /** Advance the timeline by the given timespan. Returns false when the timeline has completed. */
    def advance(span: TimeSpan): Boolean

    /** The time relative to the start of the timeline pointed to by this cursor. */
    def time: TimePoint

    /** Returns true if the cursor is at the end of the timeline (the `advance` method has turned false). */
    def done: Boolean

    /** Abort the timeline instance. The `done` method should return true immediately after calling this. */
    def abort(): Unit
  }

  trait TimelineContext[T <: Timeline] {
    def self: T
    def cursor: T#Cursor
    def time: TimePoint = cursor.time
  }

  abstract class LazyPassThruCursor extends Cursor {
    def timeline: Timeline
    private var cursor: Option[Timeline#Cursor] = None
    override def advance(span: TimeSpan): Boolean = {
      if (cursor.isEmpty) {
        cursor = Some(timeline.instantiate())
      }
      cursor.exists(_.advance(span))
    }
    override def time: TimePoint = cursor.map(_.time).getOrElse(0)
    override def done: Boolean = cursor.exists(_.done)
    override def abort(): Unit = cursor.foreach(_.abort())
  }
}

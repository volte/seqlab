package seqlab.core.timeline

import seqlab.core.{TimePoint, TimeSpan}

/**
  * A timeline represents behaviour that executes over time.
  */
trait Timeline {

  /**
    * A timeline cursor represents the current execution state of a timeline.
    */
  trait Cursor {

    /** Advance the timeline by the given timespan. Returns false when the timeline has completed. */
    def advance(span: TimeSpan): Boolean

    /** The time relative to the start of the timeline pointed to by this cursor. */
    def time: TimePoint

    /** Returns true if the cursor is at the end of the timeline (the `advance` method has turned false). */
    def done: Boolean

    /** Abort the timeline instance. The `done` method should return true immediately after calling this. */
    def abort(): Unit
  }

  /** Create a new instance of the timeline and return the cursor to its beginning. */
  def create(): this.type#Cursor
}

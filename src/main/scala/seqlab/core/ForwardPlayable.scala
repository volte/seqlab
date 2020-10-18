package seqlab.core

import java.util.concurrent.Flow._

/**
 * A `Playable` can provide a cursor to its beginning that can be advanced forward in time and provides a stream
 * of control events.
 */
trait ForwardPlayable {
  /** A cursor to a particular point in a sequence. */
  trait Cursor {
    /** Advance the cursor by the given time span and return any events that have occurred in the time between
     * the old time and the new time. */
    def advance(by: Long): Unit

    /** The current time. */
    def time: TimeOffset

    /** Source stream for this cursor. */
    def events: Publisher[PlayableEvent]
  }

  /** Materialize a cursor to the beginning of the sequence. */
  def materialize(implicit context: Context): Cursor
}

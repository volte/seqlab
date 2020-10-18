package seqlab.core

/**
  * The simplest possible sequence. The only operation returns a cursor to the beginning of the sequence.
  *
  * @tparam T The time type
  */
trait ForwardSequence[T <: TimeUnit] {
  /** A cursor to a particular point in a sequence. */
  trait Cursor {
    /** Advance the cursor by the given time span and return any events that have occurred in the time between
      * the old time and the new time. */
    def advance(by: T#Span): Seq[Event[Any, T]]

    /** The current time. */
    def time: T#Point

    /** The status of the cursor. */
    def status: SequenceStatus
  }

  /** Return a cursor to the beginning of the sequence. */
  def begin: Cursor
}

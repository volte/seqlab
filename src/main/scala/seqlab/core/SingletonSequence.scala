package seqlab.core

case class SingletonSequence[D, T <: TimeUnit](data: D)(implicit val timeUnit: T) extends ForwardSequence[T] {
  case class SingletonCursor(data: D) extends Cursor {
    private var _status: SequenceStatus = SequenceStatus.NotStarted

    /** Advance the cursor by the given time span and return any events that have occurred in the time between
     * the old time and the new time. */
    override def advance(by: T#Span): Seq[Event[Any, T]] = {
      _status = SequenceStatus.Finished
      Seq(Event(timeUnit.origin, data))
    }

    /** The current time. */
    override def time: T#Point = timeUnit.origin

    /** The status of the cursor. */
    override def status: SequenceStatus = _status
  }

  /** Return a cursor to the beginning of the sequence. */
  override def begin: Cursor = SingletonCursor(data)
}

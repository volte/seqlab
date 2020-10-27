package seqlab.core

/**
  * A timeline is a chronological arrangement of events.
  */
trait Timeline[E] {
  def advance(span: TimeSpan): Seq[ScheduledEvent[E]]
  def time: Long
  def done: Boolean
}

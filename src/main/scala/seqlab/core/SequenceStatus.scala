package seqlab.core

/** Base class for the status of a sequence cursor. */
sealed trait SequenceStatus

object SequenceStatus {
  /** The sequence has not been started yet. */
  final case object NotStarted extends SequenceStatus

  /** The sequence is in progress. */
  final case object InProgress extends SequenceStatus

  /** The sequence has finished. */
  final case object Finished extends SequenceStatus

  /** The sequence failed with an error. */
  final case class Failed(error: Error) extends SequenceStatus
}
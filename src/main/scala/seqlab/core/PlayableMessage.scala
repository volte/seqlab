package seqlab.core

/**
 * The base type for the enumeration of events raised in the course of playing a playable.
 */
sealed trait PlayableMessage

object PlayableMessage {
  /** The playable has started. Always emitted as the very first event after a `Playable` begins. */
  case object Started extends PlayableMessage

  /** A message has been raised by the playable. */
  case class Data[+T](data: T) extends PlayableMessage

  /** A message has been raised by the playable. */
  case object Finished extends PlayableMessage
}
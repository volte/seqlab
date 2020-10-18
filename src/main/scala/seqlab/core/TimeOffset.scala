package seqlab.core

/**
 * A tick is the smallest unit of time representable in a playable.
 */
case class TimeOffset(offset: Long) {
  def +(other: Long): TimeOffset = TimeOffset(offset + other)
  def -(other: TimeOffset): Long = offset - other.offset
}

object TimeOffset {
  implicit def fromLong(value: Long): TimeOffset = TimeOffset(value)
}
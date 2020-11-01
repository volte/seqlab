package seqlab.core

/** Represents a span of time. */
case class TimeSpan(ticks: Long) extends TimeSpanOps

object TimeSpan {
  implicit def ordered(timePoint: TimeSpan): Ordered[TimeSpan] =
    (that: TimeSpan) => timePoint.ticks.compare(that.ticks)

  implicit def fromLong(ticks: Long): TimeSpan = TimeSpan(ticks)
}

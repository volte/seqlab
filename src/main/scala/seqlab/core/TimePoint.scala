package seqlab.core

/** Represents a point in time. */
case class TimePoint(ticks: Long) extends TimePointOps

object TimePoint {
  implicit def ordered(timePoint: TimePoint): Ordered[TimePoint] =
    (that: TimePoint) => timePoint.ticks.compare(that.ticks)

  implicit def fromLong(ticks: Long): TimePoint = TimePoint(ticks)
}

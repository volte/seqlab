package seqlab.core

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/** Represents a point in time. */
case class TimePoint(ticks: Long) extends TimePointOps

object TimePoint {
  implicit def ordered(timePoint: TimePoint): Ordered[TimePoint] =
    (that: TimePoint) => timePoint.ticks.compare(that.ticks)

  implicit def fromLong(ticks: Long): TimePoint =
    TimePoint(ticks)

  implicit def fromFiniteDuration(duration: FiniteDuration)(implicit timeScale: TimeScale): TimePoint =
    TimePoint((timeScale.ticksPerSecond * (duration.toNanos.toDouble / 1.second.toNanos)).toLong)
}

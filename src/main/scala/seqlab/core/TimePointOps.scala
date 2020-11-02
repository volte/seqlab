package seqlab.core

import scala.concurrent.duration.{Duration, DurationInt}

trait TimePointOps { this: TimePoint =>
  def +(other: TimeSpan): TimePoint = TimePoint(ticks + other.ticks)
  def -(other: TimePoint): TimeSpan = TimeSpan(ticks - other.ticks)

  def toDuration(implicit timeScale: TimeScale): Duration =
    (ticks.toDouble / timeScale.ticksPerSecond) * 1.second
}

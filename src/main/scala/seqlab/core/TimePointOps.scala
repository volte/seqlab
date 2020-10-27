package seqlab.core

trait TimePointOps { this: TimePoint =>
  def +(other: TimeSpan): TimePoint = TimePoint(ticks + other.ticks)
  def -(other: TimePoint): TimeSpan = TimeSpan(ticks - other.ticks)
}

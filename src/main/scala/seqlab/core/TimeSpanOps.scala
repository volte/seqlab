package seqlab.core

trait TimeSpanOps { this: TimeSpan =>
  def +(other: TimeSpan): TimeSpan = TimeSpan(ticks + other.ticks)
  def -(other: TimeSpan): TimeSpan = TimeSpan(ticks - other.ticks)
  def *[T](scalar: T)(implicit ev: Numeric[T]): TimeSpan =
    TimeSpan(ev.toLong(scalar) * ticks)
  def /[T](scalar: T)(implicit ev: Numeric[T]): TimeSpan =
    TimeSpan((ev.toFloat(scalar) / ticks).toLong)
}

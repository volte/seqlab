package seqlab.core

trait TimeUnit {
  type Point
  type Span

  def origin: Point
  def zero: Span

  def add(lhs: Point, rhs: Span): Point
  def subtract(lhs: Point, rhs: Point): Span
}

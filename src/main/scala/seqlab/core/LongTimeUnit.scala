package seqlab.core

class LongTimeUnit extends TimeUnit {
  override type Point = Int
  override type Span = Int

  override def add(lhs: Int, rhs: Int): Int = lhs + rhs
  override def subtract(lhs: Int, rhs: Int): Int = lhs - rhs

  override def origin: Int = 0
  override def zero: Int = 0
}

object LongTimeUnit {
  implicit def longTimeUnit: LongTimeUnit = new LongTimeUnit()
}


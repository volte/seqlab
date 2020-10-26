package seqlab.midi

import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait TempoOps { self: Timing.Tempo =>
  def zero: FiniteDuration = 0.nanoseconds
  def wholeNote: FiniteDuration = self.quarterNote * 4
  def halfNote: FiniteDuration = self.quarterNote * 2
  def eighthNote: FiniteDuration = self.quarterNote / 2
  def sixteenthNote: FiniteDuration = self.quarterNote / 4
}

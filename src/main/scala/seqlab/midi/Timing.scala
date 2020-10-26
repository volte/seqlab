package seqlab.midi

import scala.concurrent.duration.{DurationDouble, FiniteDuration}

/**
  * Translates musical timings based on the MIDI standard into clock time.
  * @param ticksPerQuarterNote The number of ticks
  */
case class Timing(ticksPerQuarterNote: Int = 960) {
  def tempo(bpm: Int): Timing.Tempo = Timing.Tempo(this, bpm)
}

object Timing {
  case class Tempo private (timing: Timing, bpm: Int) extends TempoOps {
    def ticksPerSecond: Int = timing.ticksPerQuarterNote * (bpm / 60)
    def quarterNote: FiniteDuration = 1.minute / bpm
    def tick: FiniteDuration = quarterNote / timing.ticksPerQuarterNote
  }
}

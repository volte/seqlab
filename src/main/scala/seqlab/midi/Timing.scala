package seqlab.midi

import scala.concurrent.duration.{DurationDouble, FiniteDuration}

/**
  * Translates musical timings based on the MIDI standard into clock time.
  * @param ticksPerQuarterNote The number of ticks
  */
case class Timing(ticksPerQuarterNote: Int) {
  case class Tempo private (bpm: Int) {
    def quarterNote: FiniteDuration = 1.seconds / bpm
    def tick: FiniteDuration = quarterNote / ticksPerQuarterNote
  }

  def tempo(bpm: Int): Tempo = Tempo(bpm)
}

package seqlab.midi

import scala.concurrent.duration.{DurationDouble, FiniteDuration}

/**
  * Translates musical timings based on the MIDI standard into clock time.
  */
case class Timing(pulsesPerQuarterNote: Int = 960) extends TimingOps {
  case class Tempo private (bpm: Int) {
    def pulsesPerSecond: Int = (pulsesPerQuarterNote * bpm) / 60
  }

  def tempo(bpm: Int): Tempo = Tempo(bpm)
}

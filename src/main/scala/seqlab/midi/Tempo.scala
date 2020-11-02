package seqlab.midi

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class Tempo(bpm: Int) {
  def beatDuration: FiniteDuration =
    1.minute / bpm
}

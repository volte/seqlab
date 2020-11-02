package seqlab.midi

import javax.sound.midi.MetaMessage

import scala.concurrent.duration.DurationInt

object MidiUtils {
  def tempoMessage(bpm: Int): MetaMessage = {
    val mpqn = 1.minute.toMicros / bpm
    val data = new Array[Byte](3)
    for (i <- 0 until 3) {
      val shift = (3 - 1 - i) * 8
      data(i) = (mpqn >> shift).toByte
    }
    new MetaMessage(81, data, 3)
  }
}

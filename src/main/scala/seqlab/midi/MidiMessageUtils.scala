package seqlab.midi

import javax.sound.midi.{MetaMessage, ShortMessage}

import scala.concurrent.duration.DurationInt

object MidiMessageUtils {
  def tempo(bpm: Int): MetaMessage = {
    val mpqn = 1.minute.toMicros / bpm
    val data = new Array[Byte](3)
    for (i <- 0 until 3) {
      val shift = (3 - 1 - i) * 8
      data(i) = (mpqn >> shift).toByte
    }
    new MetaMessage(81, data, 3)
  }

  def noteOn(channel: Int, note: MidiNote, velocity: Int): ShortMessage = {
    new ShortMessage(ShortMessage.NOTE_ON, channel, note.number, velocity)
  }

  def noteOff(channel: Int, note: MidiNote, velocity: Int): ShortMessage = {
    new ShortMessage(ShortMessage.NOTE_OFF, channel, note.number, velocity)
  }
}

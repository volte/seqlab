package seqlab.midi

import javax.sound.midi.MidiMessage

trait MidiChannel {
  def raw(timestamp: Long, message: MidiMessage)
}

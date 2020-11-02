package seqlab.midi

case class MidiNote(number: Int)

object MidiNote {
  implicit class MidiNoteOps(midiNote: MidiNote) {
    def sharp: MidiNote = MidiNote(midiNote.number + 1)
    def flat: MidiNote = MidiNote(midiNote.number - 1)
  }
}

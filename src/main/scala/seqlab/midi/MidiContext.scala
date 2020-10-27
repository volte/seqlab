package seqlab.midi

import seqlab.program.BaseContext

trait MidiContext[C <: MidiContext[C]] extends BaseContext[C] {
  def midiEngine: MidiEngine
  def tempo: Timing#Tempo
}

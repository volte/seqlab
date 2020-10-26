package seqlab.midi

import seqlab.core.actors.Sequencer.SequencerActorRef
import seqlab.program.{BaseContext, Instruction}

trait MidiContext[C <: MidiContext[C]] extends BaseContext[C] {
  def engine: MidiEngine
  def tempo: Timing.Tempo
  def midiTicks: Long
}

object MidiContext {
  case class State(engine: MidiEngine, tempo: Timing.Tempo, midiTicks: Long)
}

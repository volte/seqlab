package seqlab.midi.instructions

import javax.sound.midi.ShortMessage
import seqlab.core.ScheduledOps.ArrowOperator
import seqlab.midi.MidiContext
import seqlab.program.Instruction

/**
  * Play a particular MIDI note.
  */
case class PlayNote[C <: MidiContext[C]](channel: Int, note: Int, velocity: Int, duration: Long)
    extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit = {
    context.emit(
      0L -->: SendMidi[C](new ShortMessage(ShortMessage.NOTE_ON, channel, note, velocity)),
      duration -->: SendMidi[C](new ShortMessage(ShortMessage.NOTE_OFF, channel, note, velocity))
    )
  }
}

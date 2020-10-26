package seqlab.midi.instructions

import javax.sound.midi.{MidiMessage, ShortMessage}
import seqlab.core.ScheduledEvent.FiniteDurationOperator
import seqlab.core.actors.Sequencer
import seqlab.midi.MidiContext
import seqlab.program.Instruction

import scala.concurrent.duration.FiniteDuration

/**
  * Play a particular MIDI note.
  */
case class PlayNote[C <: MidiContext[C]](channel: Int, note: Int, velocity: Int, duration: FiniteDuration)
    extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit = {
    context.sequencerActor ! Sequencer.AddEvents(
      context.tempo.zero @> SendMidi[C](new ShortMessage(ShortMessage.NOTE_ON, channel, note, velocity)),
      duration @> SendMidi[C](new ShortMessage(ShortMessage.NOTE_OFF, channel, note, velocity))
    )
  }
}

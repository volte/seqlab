package seqlab.midi.instructions

import javax.sound.midi.MidiMessage
import seqlab.midi.{MidiContext, MidiEngine}
import seqlab.program.{BaseContext, Instruction}

/**
  * Send a MIDI message to the given MIDI engine.
  */
case class SendMidi[C <: MidiContext[C]](message: MidiMessage) extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit = {
    for (device <- context.engine.device) {
      device.getReceiver.send(message, context.midiTicks)
    }
  }
}

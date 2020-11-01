package seqlab.program.instruction

import seqlab.core.timeline.Timeline

case class Start(timeline: Timeline) extends Instruction[MixerContext] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: MixerContext): Unit =
    context.mixerCursor.start(timeline)
}

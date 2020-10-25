package seqlab.program

import seqlab.core.actors.Sequencer

/**
  * The context in which sequencer program instructions are executed.
  */
trait ExecutionContext {

  /** The sequencer that assembles instructions chronologically. */
  def sequencerActor: Sequencer.SequencerActorRef[Instruction]

  /** The elapsed time in nanoseconds. */
  def time: Long
}

object ExecutionContext {
  def apply(sequencerActor0: Sequencer.SequencerActorRef[Instruction], time0: Long): ExecutionContext =
    new ExecutionContext {
      override def sequencerActor: Sequencer.SequencerActorRef[Instruction] = sequencerActor0
      override def time: Long = time0
    }
}

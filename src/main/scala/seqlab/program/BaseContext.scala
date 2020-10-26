package seqlab.program

import seqlab.core.actors.Sequencer

/**
  * Base context that instructions must implement in order to be compatible with the program executor.
  */
trait BaseContext[Self <: BaseContext[Self]] {

  /** The sequencer that assembles instructions chronologically. */
  def sequencerActor: Sequencer.SequencerActorRef[Instruction[Self]]

  /** The elapsed time in nanoseconds. */
  def time: Long
}

object BaseContext {
  case class State[C <: BaseContext[C]](sequencerActor: Sequencer.SequencerActorRef[Instruction[C]], time: Long)
}

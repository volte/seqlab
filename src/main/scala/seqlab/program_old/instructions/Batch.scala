package seqlab.program_old.instructions

import seqlab.program_old.{BaseContext, Instruction}

/**
  * Execute multiple instructions immediately, in sequence.
  */
case class Batch[C <: BaseContext[C]](instructions: Seq[Instruction[C]]) extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit = {
    instructions.foreach(_.execute(context))
  }
}

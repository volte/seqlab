package seqlab.program.instructions

import seqlab.program.{ExecutionContext, Instruction}

/**
 * Execute multiple instructions immediately, in sequence.
 */
case class Batch(instructions: Seq[Instruction]) extends Instruction {
  /**
   * Execute a this instruction in the given context.
   */
  override def execute(context: ExecutionContext): Unit = {
    instructions.foreach(_.execute(context))
  }
}

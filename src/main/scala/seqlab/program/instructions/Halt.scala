package seqlab.program.instructions

import seqlab.program.{BaseContext, Instruction}

case class Halt[C <: BaseContext[C]]() extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit =
    context.break()
}

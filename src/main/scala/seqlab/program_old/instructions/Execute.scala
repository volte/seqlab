package seqlab.program_old.instructions

import seqlab.program_old.{BaseContext, Instruction}

case class Execute[C <: BaseContext[C]](fn: C => Unit) extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit = fn(context)
}

package seqlab.program.instructions

import seqlab.program.{ExecutionContext, Instruction}

case class Execute(fn: ExecutionContext => Unit) extends Instruction {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: ExecutionContext): Unit = fn(context)
}

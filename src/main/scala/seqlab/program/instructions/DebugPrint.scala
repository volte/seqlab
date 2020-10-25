package seqlab.program.instructions

import seqlab.program.{ExecutionContext, Instruction}

case class DebugPrint[T](fn: ExecutionContext => T) extends Instruction {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: ExecutionContext): Unit = {
    println(s"${System.nanoTime()} - ${context.time} - ${fn(context)}")
  }
}

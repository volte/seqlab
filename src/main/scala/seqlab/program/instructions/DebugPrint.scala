package seqlab.program.instructions

import seqlab.program.{BaseContext, Instruction}

case class DebugPrint[T, C <: BaseContext[C]](fn: C => T) extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit = {
    println(s"${System.nanoTime()} - ${context.time} - ${fn(context)}")
  }
}

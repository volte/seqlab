package seqlab.program

import seqlab.program.instruction.Instruction

trait Executor[C] {
  def execute(context: C, instruction: Instruction[C]): C
}

object Executor {
  implicit def defaultExecutor[C]: Executor[C] = Executor()

  def apply[C](): Executor[C] =
    (context: C, instruction: Instruction[C]) => {
      instruction.execute(context)
      context
    }
}

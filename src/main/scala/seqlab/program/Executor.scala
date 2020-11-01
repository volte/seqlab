package seqlab.program

import seqlab.program.instruction.Instruction

/**
  * An executor manages an execution context.
  */
abstract class Executor[C] {

  /** The initial context */
  def initialContext: C

  /** The current process context */
  var context: C = initialContext

  /** Execute the given instruction. */
  def execute(instruction: Instruction[C]): Unit =
    instruction.execute(context)
}

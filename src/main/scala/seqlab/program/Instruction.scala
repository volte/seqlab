package seqlab.program

/**
  * An instruction is the unit of work in a sequenced program.
  */
trait Instruction extends InstructionOps {

  /**
    * Execute a this instruction in the given context.
    */
  def execute(context: ExecutionContext): Unit
}

object Instruction {
}

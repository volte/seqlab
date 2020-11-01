package seqlab.program.instruction

/**
  * An instruction is the unit of work in a sequenced program.
  */
trait Instruction[-C] {

  /**
    * Execute a this instruction in the given context and return the next context.
    */
  def execute(context: C): Unit
}

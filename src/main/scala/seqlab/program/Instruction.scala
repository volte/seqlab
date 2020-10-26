package seqlab.program

/**
  * An instruction is the unit of work in a sequenced program.
  */
trait Instruction[-C] {

  /**
    * Execute a this instruction in the given context.
    */
  def execute(context: C): Unit
}

package seqlab.program.instruction

case class DebugPrint(value: Any) extends Instruction[Any] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: Any): Unit =
    println(value)
}

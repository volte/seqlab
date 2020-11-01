package seqlab.program.instruction

/** Do nothing */
case object Nop extends Instruction[Any] {
  override def execute(context: Any): Unit = {}
}

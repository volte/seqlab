package seqlab.program.instructions

import seqlab.program.{BaseContext, Instruction, Program}

/**
  * Schedule the given instructions relative to the current time.
  */
case class ScheduleRelative[C <: BaseContext[C]](programFactory: C => Program[C]) extends Instruction[C] {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: C): Unit = {
    val program = programFactory(context)
    context.emit(program.instructions: _*)
  }
}

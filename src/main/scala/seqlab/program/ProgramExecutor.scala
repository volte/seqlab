package seqlab.program

import seqlab.core.actors.Sequencer
import seqlab.program.Program.ScheduledInstruction

/**
   A trait that defines how a program is executed.
  * @tparam C The context in which instructions will execute
  */
trait ProgramExecutor[C <: BaseContext[C]] {

  /**
    * Return the initial context.
    */
  def initialContext: C

  /**
    * Execute the specified instruction in the specified context and return a modified context.
    */
  def execute(instruction: ScheduledInstruction[C], context: C): C
}

package seqlab.program

import seqlab.core.Scheduled
import seqlab.core.ScheduledOps.ArrowOperator

/**
  * A program is a sequence of instructions that execute in a certain type of context.
  */
trait Program[C] extends ProgramOps[C] {
  import Program.ScheduledInstruction

  /**
    * Return the sequence of instructions in this program.
    */
  def instructions: Seq[ScheduledInstruction[C]]
}

object Program {
  type ScheduledInstruction[C] = Scheduled[Instruction[C]]

  object ScheduledInstruction {
    def apply[C](time0: Long, instruction0: Instruction[C]): ScheduledInstruction[C] =
      Scheduled(time0, instruction0)
  }

  def apply[C](instructions0: ScheduledInstruction[C]*): Program[C] =
    new Program[C] {
      def instructions: Seq[ScheduledInstruction[C]] = instructions0
    }

  implicit def singleton[C](instruction: ScheduledInstruction[C]): Program[C] =
    Program(instruction)

  implicit def singleton[C](instruction: Instruction[C]): Program[C] =
    Program(0 -->: instruction)

  def merge[C](programs: Program[C]*): Program[C] =
    Program(
      programs.foldLeft(Seq[Program.ScheduledInstruction[C]]())((acc, prog) => acc.concat(prog.instructions)): _*
    )
}

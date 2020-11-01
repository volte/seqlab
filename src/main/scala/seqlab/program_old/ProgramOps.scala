package seqlab.program_old

import seqlab.core.ScheduledOps.ArrowOperator
import seqlab.program_old.instructions.ScheduleRelative

/**
  * Extended operations on `Program`.
  */
trait ProgramOps[C] { this: Program[C] =>
  import Program.ScheduledInstruction

  /** Map a function over this program, mapping each instruction to a different instruction. */
  def map[D](fn: ScheduledInstruction[C] => ScheduledInstruction[D]): Program[D] =
    Program(instructions.map(fn): _*)

  /** Map a function over this program, mapping each instruction to zero or more instructions in sequence. */
  def flatMap[D](fn: ScheduledInstruction[C] => Program[D]): Program[D] =
    Program(instructions.flatMap(fn(_).instructions): _*)

  /** Return a program whose instructions have been filtered according to a function. */
  def filter(fn: ScheduledInstruction[C] => Boolean): Program[C] =
    Program(instructions.filter(fn): _*)

  /** Return a program that repeats forever at the specified interval. */
  def loopForever[D <: BaseContext[D]](
      interval: Long
  )(implicit ev: Instruction[C] <:< Instruction[D]): Program[D] =
    Program.merge[D](this.map(_.map(ev)), interval -->: ScheduleRelative[D](_ => loopForever[D](interval)))

}

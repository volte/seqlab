package seqlab.program

/**
  * Extended operations on `Program`.
  */
trait ProgramOps { this: Program =>
  import Program.ScheduledInstruction

  /** Map a function over this program, mapping each instruction to a different instruction. */
  def map(fn: ScheduledInstruction => ScheduledInstruction): Program =
    Program(instructions.map(fn) : _*)

  /** Map a function over this program, mapping each instruction to zero or more instructions in sequence. */
  def flatMap(fn: ScheduledInstruction => Program): Program =
    Program(instructions.flatMap(fn(_).instructions) : _*)

  /** Return a program whose instructions have been filtered according to a function. */
  def filter(fn: ScheduledInstruction => Boolean): Program =
    Program(instructions.filter(fn) : _*)

  /** Return a program with the instructions of multiple programs compiled. */
  def merge(programs: Program*): Program =
    Program(
      programs
        .foldLeft(instructions)((acc, prog) => acc.concat(prog.instructions)) : _*)
}

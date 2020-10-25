package seqlab.program

import scala.concurrent.duration.FiniteDuration

/**
 * Extended operations on `Instruction`.
 */
trait InstructionOps { this: Instruction =>
  def at(time: FiniteDuration): Program.ScheduledInstruction =
    Program.ScheduledInstruction(time.toNanos, this)
}

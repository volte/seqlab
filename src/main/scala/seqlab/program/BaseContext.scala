package seqlab.program

import scala.concurrent.duration.FiniteDuration

/**
  * Base context that instructions must implement in order to be compatible with the program executor.
  */
trait BaseContext[Self <: BaseContext[Self]] {

  /** The elapsed time in ticks. */
  def time: Long

  /** Emit some instructions into the program. */
  def emit(instructions: Program.ScheduledInstruction[Self]*): Unit

  /** Halt the current program. */
  def break(): Unit
}

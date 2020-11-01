package seqlab.program

import seqlab.core.{ScheduledQueue, TimePoint, TimeSpan}
import seqlab.core.timeline.Timeline
import seqlab.program.instruction.Instruction
import seqlab.util.AutoCloneable

class Process[C <: AutoCloneable[C]]( //
    program: ScheduledQueue[Instruction[C]],
    initialContext: C
)(implicit
    val executor: Executor[C]
) extends Timeline {
  class ProgramCursor extends Cursor {
    val _program: ScheduledQueue[Instruction[C]] = program.clone()
    var context: C = initialContext.clone()

    /** Advance the timeline by the given timespan. Returns false when the timeline has completed. */
    override def advance(span: TimeSpan): Boolean = {
      val instructions = _program.dequeue(span)
      for (instruction <- instructions) {
        context = executor.execute(context, instruction.data)
      }
      !_program.isEmpty
    }

    /** The time relative to the start of the timeline pointed to by this cursor. */
    override def time: TimePoint =
      _program.time

    /** Returns true if the cursor is at the end of the timeline (the `advance` method has turned false). */
    override def done: Boolean =
      _program.isEmpty

    /** Abort the timeline instance. The `done` method should return true immediately after calling this. */
    override def abort(): Unit =
      _program.clear()
  }

  /** Create a new instance of the timeline and return the cursor to its beginning. */
  override def instantiate(): Cursor =
    new ProgramCursor()
}

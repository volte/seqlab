package seqlab.program

import seqlab.core.{ScheduledQueue, TimePoint, TimeSpan}
import seqlab.core.timeline.Timeline
import seqlab.program.instruction.Instruction

class Process[C](program: ScheduledQueue[Instruction[C]], executor: Executor[C]) extends Timeline {
  class ProgramCursor extends Cursor {

    /** Advance the timeline by the given timespan. Returns false when the timeline has completed. */
    override def advance(span: TimeSpan): Boolean = {
      val instructions = program.dequeue(span)
      for (instruction <- instructions) {
        executor.execute(instruction.data)
      }
      !program.isEmpty
    }

    /** The time relative to the start of the timeline pointed to by this cursor. */
    override def time: TimePoint =
      program.time

    /** Returns true if the cursor is at the end of the timeline (the `advance` method has turned false). */
    override def done: Boolean =
      program.isEmpty

    /** Abort the timeline instance. The `done` method should return true immediately after calling this. */
    override def abort(): Unit =
      program.clear()
  }

  /** Create a new instance of the timeline and return the cursor to its beginning. */
  override def create(): Cursor =
    new ProgramCursor()
}

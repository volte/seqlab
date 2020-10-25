package seqlab.program.instructions

import seqlab.core.actors.Sequencer
import seqlab.program.{ExecutionContext, Instruction, Program}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
  * Schedule the given instructions relative to the current time.
  */
case class ScheduleRelative(programFactory: ExecutionContext => Program, offset: FiniteDuration = 0.nanoseconds)
    extends Instruction {

  /**
    * Execute a this instruction in the given context.
    */
  override def execute(context: ExecutionContext): Unit = {
    val program = programFactory(context)
    context.sequencerActor ! Sequencer.AddEvents(program.instructions.map(_.mapTime(_ + context.time + offset.toNanos)))
  }
}

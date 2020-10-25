package seqlab.program

import akka.actor.typed.ActorRef
import seqlab.core.ScheduledEvent
import seqlab.core.actors.Sequencer
import seqlab.program.instructions.ScheduleRelative

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
  * A program is a sequence of instructions.
  */
trait Program extends ProgramOps {
  import Program.ScheduledInstruction

  /**
    * Return the sequence of instructions in this program, ordered by start time.
    */
  def instructions: Seq[ScheduledInstruction]
}

object Program {
  type ScheduledInstruction = ScheduledEvent[Instruction]
  type SequencerActor = ActorRef[Sequencer.SequencerCommand[Instruction]]

  object ScheduledInstruction {
    def apply(time0: Long, instruction0: Instruction): ScheduledInstruction =
      ScheduledEvent(time0, instruction0)
  }

  def apply(instructions0: ScheduledInstruction*): Program =
    new Program {
      def instructions: Seq[ScheduledInstruction] = instructions0
    }

  implicit def singleton(instruction: ScheduledInstruction): Program =
    Program(instruction)

  implicit def singleton(instruction: Instruction): Program =
    Program(instruction.at(0.nanoseconds))

  def repeat(program: Program, interval: FiniteDuration): Program =
    program.merge(ScheduleRelative(_ => repeat(program, interval)).at(interval))
}

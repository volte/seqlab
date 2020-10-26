package seqlab.program.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import seqlab.core.actors.Sequencer
import seqlab.program.{BaseContext, Instruction, Program, ProgramExecutor}

object InstructionRunner {
  private def runner[C <: BaseContext[C], P <: ProgramExecutor[C]](
      sequencerActor: Sequencer.SequencerActorRef[Instruction[C]],
      executor: P,
      context: C
  ): Behavior[Program.ScheduledInstruction[C]] =
    Behaviors.receiveMessage { message =>
      val nextContext = executor.execute(message, context)
      runner(sequencerActor, executor, nextContext)
    }

  def apply[C <: BaseContext[C], P <: ProgramExecutor[C]](
      sequencerActor: Sequencer.SequencerActorRef[Instruction[C]],
      executor: P
  ): Behavior[Program.ScheduledInstruction[C]] =
    runner(sequencerActor, executor, executor.initialContext)
}

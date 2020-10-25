package seqlab.program.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import seqlab.core.actors.Sequencer
import seqlab.program.{ExecutionContext, Instruction, Program}

object ProgramExecutor {
  private def executor(
      sequencerActor: Sequencer.SequencerActorRef[Instruction]
  ): Behavior[Program.ScheduledInstruction] =
    Behaviors.receive { (context, message) =>
      val context = ExecutionContext(sequencerActor, message.time)
      message.data.execute(context)
      Behaviors.same
    }

  def apply(sequencerActor: Sequencer.SequencerActorRef[Instruction]): Behavior[Program.ScheduledInstruction] =
    executor(sequencerActor)
}

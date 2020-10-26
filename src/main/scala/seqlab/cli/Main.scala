package seqlab.cli

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import seqlab.core.actors.Sequencer
import seqlab.core.actors.Sequencer.SequencerActorRef
import seqlab.midi.instructions.PlayNote
import seqlab.midi.{MidiContext, MidiEngine, Timing}
import seqlab.program.Program.ScheduledInstruction
import seqlab.program.actors.InstructionRunner
import seqlab.program.{BaseContext, Instruction, Program, ProgramExecutor}

object Main {
  case class Context(base: BaseContext.State[Context], midi: MidiContext.State) extends MidiContext[Context] {
    override def engine: MidiEngine = midi.engine
    override def midiTicks: Long = midi.midiTicks
    override def sequencerActor: SequencerActorRef[Instruction[Context]] = base.sequencerActor
    override def time: Long = base.time
    override def tempo: Timing.Tempo = midi.tempo
  }

  case class Executor(sequencerActor: SequencerActorRef[Instruction[Context]], midiEngine: MidiEngine)
      extends ProgramExecutor[Context] {
    override def initialContext: Context =
      Context(
        BaseContext.State[Context](sequencerActor, 0),
        MidiContext.State(midiEngine, midiEngine.timing.tempo(120), 0)
      )

    override def execute(instruction: ScheduledInstruction[Context], context: Context): Context = {
      val time = instruction.time
      val midiTicks = time / context.midi.tempo.ticksPerSecond
      val thisContext = Context(
        BaseContext.State[Context](context.base.sequencerActor, instruction.time),
        MidiContext.State(context.midi.engine, context.midi.tempo, midiTicks)
      )
      instruction.data.execute(thisContext)
      thisContext
    }
  }

  object RootActor {
    trait Message
    case object Start extends Message

    def apply(midiEngine: MidiEngine): Behavior[Message] =
      Behaviors.setup { context =>
        import seqlab.core.ScheduledEvent
        val sequencer = context.spawn(Sequencer[Instruction[Context]](), "sequencer")
        val executor = Executor(sequencer, midiEngine)
        val runner = context.spawn(InstructionRunner[Context, Executor](sequencer, executor), "executor")

        import ScheduledEvent.FiniteDurationOperator

        val tempo = midiEngine.timing.tempo(140)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val bassAndSnare = Program[Context](
          tempo.zero @> PlayNote(9, 35, 100, tempo.eighthNote),
          tempo.quarterNote @> PlayNote(9, 35, 100, tempo.eighthNote),
          (2 * tempo.quarterNote) @> PlayNote(9, 38, 100, tempo.eighthNote)
        ).loopForever(tempo.wholeNote)

        var hihat = Program[Context](
          tempo.zero @> PlayNote(9, 42, 100, tempo.eighthNote),
          tempo.eighthNote @> PlayNote(9, 42, 80, tempo.eighthNote)
        ).loopForever(tempo.eighthNote * 2)

        val program = Program.merge(bassAndSnare, hihat)
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        sequencer ! Sequencer.AddEvents(program.instructions: _*)

        Behaviors.receiveMessage {
          case Start =>
            sequencer ! Sequencer.Start(runner)
            Behaviors.ignore
        }
      }
  }

  def main(args: Array[String]): Unit = {
    val midiEngine = new MidiEngine(MidiEngine.Options(Seq("VirtualMIDISynth #1")))
    midiEngine.start()
    implicit val mainActorSystem: ActorSystem[RootActor.Message] = ActorSystem(RootActor(midiEngine), "rootActor")
    mainActorSystem ! RootActor.Start
  }
}

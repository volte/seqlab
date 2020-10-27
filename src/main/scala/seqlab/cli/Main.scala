package seqlab.cli

import seqlab.core.ScheduledEventOps.ArrowOperator
import seqlab.core.{MutableTimeline, Receiver, ScheduledEvent, Sequencer}
import seqlab.midi.instructions.PlayNote
import seqlab.midi.{MidiContext, MidiEngine, Timing}
import seqlab.program.Program.ScheduledInstruction
import seqlab.program.{Instruction, Program, ProgramExecutor}

object Main {
  type MidiTimeline = MutableTimeline[Instruction[Context]]
  type MidiSequencer = Sequencer[Instruction[Context], MutableTimeline[Instruction[Context]]]
  type MidiInstruction = Instruction[Context]

  case class State(
      var midiEngine: MidiEngine,
      var tempo: Timing#Tempo,
      var time: Long,
      var timeline: MidiTimeline
  )

  case class Context(state: State) extends MidiContext[Context] {
    override def midiEngine: MidiEngine = state.midiEngine
    override def tempo: Timing#Tempo = state.tempo
    override def time: Long = state.time
    override def emit(instructions: ScheduledInstruction[Context]*): Unit = {
      state.timeline.add(instructions: _*)
    }
  }

  case class MidiExecutor(midiEngine: MidiEngine, tempo: Timing#Tempo, timeline: MidiTimeline)
      extends ProgramExecutor[Context] {
    override def execute(instruction: ScheduledInstruction[Context], context: Context): Context = {
      val newState = context.state
      val time = instruction.time
      newState.time = time
      val newContext = Context(newState)
      instruction.data.execute(newContext)
      newContext
    }

    override def initialContext: Context = Context(State(midiEngine, tempo, 0, timeline))
  }

  case class MidiReceiver(executor: MidiExecutor) extends Receiver[MidiInstruction] {
    private var context = executor.initialContext

    override def receive(instruction: ScheduledEvent[MidiInstruction]): Unit = {
      val newContext = executor.execute(instruction, context)
      context = newContext
    }
  }

  def main(args: Array[String]): Unit = {
    val midiEngine = new MidiEngine(MidiEngine.Options(Seq("VirtualMIDISynth #1")))
    midiEngine.start()

    val timing = midiEngine.timing
    val tempo = midiEngine.timing.tempo(140)

    import timing.IntHelpers
    val bassAndSnare = Program[Context](
      0.n4 -->: PlayNote[Context](9, 35, 100, 1.n8),
      1.n4 -->: PlayNote[Context](9, 35, 100, 1.n8),
      2.n4 -->: PlayNote[Context](9, 38, 100, 1.n8)
    ).loopForever(1.n1)

    var hihat = Program[Context](
      0.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      1.n8 -->: PlayNote[Context](9, 42, 80, 1.n8)
    ).loopForever(2.n8)

    val program = Program.merge(bassAndSnare, hihat)

    val timeline = MutableTimeline[MidiInstruction](program.instructions: _*)
    val executor = MidiExecutor(midiEngine, tempo, timeline)
    val receiver = MidiReceiver(executor)
    val sequencer = new MidiSequencer(timeline, receiver)

    sequencer.ticksPerSecond = tempo.pulsesPerSecond
    sequencer.start()
  }
}

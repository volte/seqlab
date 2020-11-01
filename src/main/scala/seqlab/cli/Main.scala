package seqlab.cli

import seqlab.core.ScheduledEventOps.ArrowOperator
import seqlab.core.{MutableTimeline, Receiver, ScheduledEvent, Sequencer}
import seqlab.midi.instructions.PlayNote
import seqlab.midi.{MidiContext, MidiEngine, Timing}
import seqlab.program.Program.ScheduledInstruction
import seqlab.program.instructions.Halt
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
      state.timeline.add(relative = true)(instructions: _*)
    }
    override def break(): Unit =
      state.timeline.break()
  }

  case class MidiExecutor(midiEngine: MidiEngine, tempo: Timing#Tempo, timeline: MidiTimeline)
      extends ProgramExecutor[Context]
      with Receiver[MidiInstruction] {
    private var context = initialContext

    override def receive(instruction: ScheduledEvent[MidiInstruction]): Unit = {
      val newContext = execute(instruction, context)
      context = newContext
    }

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

  def main(args: Array[String]): Unit = {
    val midiEngine = new MidiEngine(MidiEngine.Options(Seq("VirtualMIDISynth #1")))
    midiEngine.start()

    val timing = midiEngine.timing
    val tempo = midiEngine.timing.tempo(200)

    import timing.IntHelpers
    val bassAndSnare = Program[Context](
      0.n4 -->: PlayNote[Context](9, 35, 100, 1.n8),
      1.n4 -->: PlayNote[Context](9, 35, 100, 1.n8),
      2.n4 -->: PlayNote[Context](9, 38, 100, 1.n8),
      3.n4 -->: PlayNote[Context](9, 35, 70, 1.n8),
      (3.n4 + 1.n8) -->: PlayNote[Context](9, 35, 100, 1.n8),
      (5.n4 - 1.n8) -->: PlayNote[Context](9, 35, 70, 1.n8),
      5.n4 -->: PlayNote[Context](9, 35, 100, 1.n8),
      6.n4 -->: PlayNote[Context](9, 38, 100, 1.n8)
    ).loopForever(2.n1)

    var hihat = Program[Context](
      0.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      1.n8 -->: PlayNote[Context](9, 42, 80, 1.n8),
      2.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      3.n8 -->: PlayNote[Context](9, 42, 80, 1.n8),
      4.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      5.n8 -->: PlayNote[Context](9, 42, 80, 1.n8),
      6.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      7.n8 -->: PlayNote[Context](9, 42, 80, 1.n8),
      8.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      9.n8 -->: PlayNote[Context](9, 42, 80, 1.n8),
      10.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      11.n8 -->: PlayNote[Context](9, 42, 80, 1.n8),
      12.n8 -->: PlayNote[Context](9, 42, 100, 1.n8),
      13.n8 -->: PlayNote[Context](9, 42, 80, 1.n8),
      14.n8 -->: PlayNote[Context](9, 46, 100, 1.n4)
    ).loopForever(2.n1)

    var haltProgram = Program[Context](
      4.n1 -->: Halt[Context]()
    )

    val program = Program.merge(bassAndSnare, hihat, haltProgram)

    val timeline = MutableTimeline[MidiInstruction](program.instructions: _*)
    val executor = MidiExecutor(midiEngine, tempo, timeline)

    val sequencer = new MidiSequencer(timeline, executor)

    sequencer.ticksPerSecond = tempo.pulsesPerSecond
    sequencer.start()
    sequencer.join()
    println("Sequencer finished")

    midiEngine.stop()
    println("Shutting down")
  }
}

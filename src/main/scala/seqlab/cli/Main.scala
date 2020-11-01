package seqlab.cli

import seqlab.core.ScheduledOps.ArrowOperator
import seqlab.core.timeline.{Looper, Mixer, TimelineRunner}
import seqlab.core.{ScheduledQueue, TimeScale}
import seqlab.program.{Process, ProcessContext}
import seqlab.program.instruction.{DebugPrint, Instruction, MixerContext, Nop}
import seqlab.util.AutoCloneable

import scala.concurrent.duration.DurationInt

object Main {
  def main(args: Array[String]): Unit = {
    implicit val timeScale: TimeScale = TimeScale(1.second.toNanos)

    class Context(val mixerCursor0: Mixer#MixerCursor, val process0: Process[Context])
        extends MixerContext
        with ProcessContext[Context]
        with AutoCloneable[Context] {
      override def mixerCursor: Mixer#MixerCursor = mixerCursor0
      override def process: Process[Context] = process0
    }

    import seqlab.program.Executor.defaultExecutor

    val oneTwoThreeFour = ScheduledQueue[Instruction[Context]](
      0.second -->: DebugPrint(1),
      1.second -->: DebugPrint(2),
      2.second -->: DebugPrint(3),
      3.second -->: DebugPrint(4),
      4.second -->: Nop
    )
    val rootMixer = new Mixer()
    val rootMixerCursor = rootMixer.instantiate()
    val process = new Process[Context](oneTwoThreeFour, new Context(rootMixerCursor))
    val loopingProcess = new Looper(process)
    rootMixerCursor.start(loopingProcess)

    val timelineRunner = new TimelineRunner(rootMixerCursor, TimelineRunner.Options(timeScale = timeScale))
    timelineRunner.start()
    timelineRunner.join()
  }
}

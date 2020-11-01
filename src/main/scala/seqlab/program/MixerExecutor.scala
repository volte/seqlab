package seqlab.program

import seqlab.core.timeline.Mixer
import seqlab.program.instruction.MixerContext

class MixerExecutor(mixer: Mixer) extends Executor[MixerContext] with MixerContext {

  /** The initial context */
  override def initialContext: MixerContext =
    new MixerContext {
      override def mixerCursor: Mixer#MixerCursor = MixerExecutor.this.mixerCursor
    }

  override val mixerCursor: Mixer#MixerCursor = mixer.create()
}

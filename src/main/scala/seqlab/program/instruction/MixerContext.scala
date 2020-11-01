package seqlab.program.instruction

import seqlab.core.timeline.Mixer

/**
  * Instructions that execute in the context of a mixer.
  */
trait MixerContext {
  def mixerCursor: Mixer#MixerCursor
}

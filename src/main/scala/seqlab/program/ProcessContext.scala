package seqlab.program

import seqlab.core.TimePoint

trait ProcessContext[Self <: ProcessContext[Self]] { this: Self =>
  def process: Process[Self]
}

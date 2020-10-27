package seqlab.midi

trait TimingOps { this: Timing =>
  implicit class IntHelpers(count: Int) {
    def n16: Int = n4 / 4
    def n8: Int = n4 / 2
    def n4: Int = pulsesPerQuarterNote * count
    def n2: Int = n4 * 2
    def n1: Int = n4 * 4
  }
}

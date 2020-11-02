package seqlab.midi

trait MidiTarget {
  type Channel <: MidiChannel

  def pulsesPerQuarterNote: Int
  def setTempo(timestamp: Long, tempo: Tempo): Unit
  def channel(index: Int): Channel
}

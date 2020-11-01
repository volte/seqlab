package seqlab.midi

import javax.sound.midi.{MetaMessage, MidiEvent, MidiSystem, Sequence, Sequencer, Track}
import seqlab.core.TimePoint

import scala.concurrent.duration.DurationInt

class MidiRecorder(options: MidiRecorder.Options) {
  private var sequencer: Sequencer = MidiSystem.getSequencer()
  private var sequence: Sequence = sequencer.getSequence
  private var tracks: List[Track] = List()

  private def initializeTracks(): Unit = {
    for (idx <- 0 until 16) {
      val track = sequence.createTrack()
      tracks :+= track
      initializeTrack(track)
    }
  }

  private def initializeTrack(track: Track): Unit = {}

  //def addTimeSignature(time: TimePoint, numerator: Int, denominator: Int)

  def addTempo(time: TimePoint, bpm: Int): Unit = {
    val mpqn = 1.minute.toMicros / bpm
    val data = new Array[Byte](3)
    for (i <- 0 until 3) {
      val shift = (3 - 1 - i) * 8
      data(i) = (mpqn >> shift).toByte
    }
    val message = new MetaMessage(81, data, 3)
    tracks.head.add(new MidiEvent(message, time.ticks))
  }
}

object MidiRecorder {
  case class Options(timing: Timing)
}

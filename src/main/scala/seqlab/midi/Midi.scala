package seqlab.midi

import javax.sound.midi.MidiMessage
import seqlab.core.{Sequence, TimeScale, Timeline}
import seqlab.core.Timeline.TimelineContext

import scala.concurrent.duration.FiniteDuration

class Midi(device: MidiTarget)(init: Midi.Init) extends Timeline {
  class Cursor extends Timeline.LazyPassThruCursor {
    override def timeline: Timeline = init(Midi.Context(Midi.this, this, device, Tempo(120)))
  }

  override def instantiate(): Cursor =
    new Cursor()
}

object Midi {
  type Init = Context => Timeline

  case class Context(self: Midi, cursor: Midi#Cursor, device: MidiTarget, var tempo: Tempo)
      extends TimelineContext[Midi] {
    def timestamp(implicit timeScale: TimeScale): Long =
      ((time.toDuration / tempo.beatDuration) * device.pulsesPerQuarterNote).toLong

    class Channel(index: Int) {
      private val channel = device.channel(index)

      def raw(message: MidiMessage)(implicit timeScale: TimeScale): Unit =
        channel.raw(timestamp, message)

      def playNote(note: MidiNote, duration: FiniteDuration, velocity: Int)(implicit timeScale: TimeScale): Timeline =
        Sequence { s =>
          s.add(0, raw(MidiMessageUtils.noteOn(index, note, velocity)))
          s.add(duration, raw(MidiMessageUtils.noteOff(index, note, velocity)))
        }
    }

    def setTempo(tempo: Tempo)(implicit timeScale: TimeScale): Unit = {
      this.tempo = tempo
      device.setTempo(timestamp, tempo)
    }

    def channel(index: Int): Channel =
      new Channel(index)
  }

  def apply(device: MidiTarget)(init: Midi.Init): Midi =
    new Midi(device)(init)
}

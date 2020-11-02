package seqlab.cli

import seqlab.core.{Looper, Mixer, Sequence, TimeScale, TimelineRunner}
import seqlab.midi.{Midi, MidiOutput, Tempo}

import scala.concurrent.duration.DurationInt

object Main {
  def main(args: Array[String]): Unit = {
    implicit val timeScale: TimeScale = TimeScale(1.second.toNanos)

    val preferredDevices = sys.env("PREFERRED_MIDI_DEVICES").split(';')

    val midiOutput = new MidiOutput(MidiOutput.Options(preferredDevices))
    val timeline = Midi(midiOutput) { midi =>
      import seqlab.midi.GenMidi.Notes._
      import seqlab.midi.GenMidi.Drums._

      midi.setTempo(Tempo(200))

      Mixer { mixer =>
        // quarter note
        val q = midi.tempo.beatDuration
        var e = q / 2

        mixer.play {
          new Looper(Sequence { s =>
            val channel = midi.channel(9)

            s.add(0 * q, mixer.play(channel.playNote(bassDrum1, q, 100)))
            s.add(1 * q, mixer.play(channel.playNote(bassDrum1, q, 80)))
            s.add(2 * q, mixer.play(channel.playNote(acousticSnare, q, 105)))
            s.add(3 * q, {})
            s.add(4 * q, {})
          })
        }

        mixer.play {
          new Looper(Sequence { s =>
            val channel = midi.channel(9)
            s.add(0 * e, mixer.play(channel.playNote(closedHighHat, q, 100)))
            s.add(1 * e, mixer.play(channel.playNote(closedHighHat, q, 80)))
            s.add(2 * e, mixer.play(channel.playNote(closedHighHat, q, 100)))
            s.add(3 * e, mixer.play(channel.playNote(closedHighHat, q, 80)))
            s.add(4 * e, mixer.play(channel.playNote(closedHighHat, q, 100)))
            s.add(5 * e, mixer.play(channel.playNote(closedHighHat, q, 80)))
            s.add(6 * e, mixer.play(channel.playNote(closedHighHat, q, 100)))
            s.add(7 * e, mixer.play(channel.playNote(closedHighHat, q, 80)))
            s.add(8 * e, {})
          })
        }
      }
    }

    val timelineRunner =
      new TimelineRunner(timeline, TimelineRunner.Options(timeScale = timeScale, burstLength = 1.second))
    timelineRunner.start()
    timelineRunner.join()
  }
}

package seqlab.cli

import seqlab.core.{Mixer, TimeScale, TimelineRunner}
import seqlab.midi.{Midi, MidiOutput}

import scala.concurrent.duration.DurationInt

object Main {
  def main(args: Array[String]): Unit = {
    implicit val timeScale: TimeScale = TimeScale(1.second.toNanos)

    val preferredDevices = sys.env("PREFERRED_MIDI_DEVICES").split(';')

    val midiOutput = new MidiOutput(MidiOutput.Options(preferredDevices))
    val timeline = Midi(midiOutput) { midi =>
      // quarter note
      val q = midi.tempo.beatDuration

      Mixer { rootMixer => }
    }

    val timelineRunner = new TimelineRunner(timeline, TimelineRunner.Options(timeScale = timeScale))
    timelineRunner.start()
    timelineRunner.join()
  }
}

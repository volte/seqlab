package seqlab.midi
import javax.sound.midi.{MetaMessage, MidiDevice, MidiMessage, MidiSystem, Receiver, Sequence}

class MidiOutput(options: MidiOutput.Options) extends MidiTarget {
  private var device: Option[MidiDevice] = None

  private val deviceInfos: Array[MidiDevice.Info] = MidiSystem.getMidiDeviceInfo
  private val deviceInfo: Option[MidiDevice.Info] =
    options.preferredDevices.flatMap(name => deviceInfos.filter(_.getName.contains(name))).headOption match {
      case None       => deviceInfos.headOption
      case deviceInfo => deviceInfo
    }

  deviceInfo match {
    case Some(deviceInfo) =>
      val device0 = MidiSystem.getMidiDevice(deviceInfo)
      device0.open()
      device = Some(device0)

    case None =>
      throw new RuntimeException("No MIDI devices available")
  }

  class Channel(index: Int) extends MidiChannel {
    override def raw(timestamp: Long, message: MidiMessage): Unit =
      device.foreach { device =>
        device.getReceiver.send(message, timestamp)
      }
  }

  override def setTempo(timestamp: Long, tempo: Tempo): Unit =
    channel(0).raw(timestamp, MidiUtils.tempoMessage(tempo.bpm))

  override def channel(index: Int): Channel =
    new Channel(index)

  override def pulsesPerQuarterNote: Int = options.pulsesPerQuarterNote

  def close(): Unit =
    device.foreach { device =>
      device.close()
    }
}

object MidiOutput {
  case class Options(preferredDevices: Seq[String], pulsesPerQuarterNote: Int = 960)
}

package seqlab.midi

import javax.sound.midi.{MidiDevice, MidiSystem}

class MidiEngine(options: MidiEngine.Options) {
  private var midiDevice: Option[MidiDevice] = None

  def start(): Unit = {
    val deviceInfos = MidiSystem.getMidiDeviceInfo
    val deviceInfo = options.preferredDevices.flatMap(name => deviceInfos.filter(_.getName == name)).headOption match {
      case None       => deviceInfos.headOption
      case deviceInfo => deviceInfo
    }
    deviceInfo match {
      case Some(deviceInfo) =>
        val device = MidiSystem.getMidiDevice(deviceInfo)
        midiDevice = Some(device)
        device.open()
      case None =>
        throw new RuntimeException("No MIDI devices available")
    }
  }

  def stop(): Unit = {
    midiDevice.foreach(_.close())
    midiDevice = None
  }

  def device: Option[MidiDevice] = midiDevice
  def timing: Timing = options.timing
}

object MidiEngine {
  case class Options(preferredDevices: Seq[String] = Seq(), timing: Timing = Timing())
}

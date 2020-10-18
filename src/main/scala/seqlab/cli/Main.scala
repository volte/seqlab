package seqlab.cli

import javax.sound.midi.{MidiSystem, ShortMessage}

object Main {
  def main(args: Array[String]): Unit = {
    val deviceInfos = MidiSystem.getMidiDeviceInfo
    println(deviceInfos.map(_.getName).mkString("Array(", ", ", ")"))
    val deviceInfo = deviceInfos.find(_.getName.contains("VirtualMIDISynth")) getOrElse {
      println("Could not open MIDI device")
      sys.exit(1)
    }
    val device = MidiSystem.getMidiDevice(deviceInfo)
    try {
      device.open()
      device.getReceiver.send(new ShortMessage(ShortMessage.NOTE_ON, 1, 70, 60), 0)
      Thread.sleep(500)
      device.getReceiver.send(new ShortMessage(ShortMessage.NOTE_OFF, 1, 70, 60), 0)
      Thread.sleep(250)
    } finally {
      device.close()
    }
  }
}

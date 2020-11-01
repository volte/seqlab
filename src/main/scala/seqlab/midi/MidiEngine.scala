package seqlab.midi

import java.io.FileOutputStream

import javax.sound.midi._

class MidiEngine(options: MidiEngine.Options) {
  private var midiDevice: Option[MidiDevice] = None
  private var midiSequencer: Option[Sequencer] = None
  private var midiSequence: Option[Sequence] = None

  def receiver: Receiver =
    new Receiver {
      override def send(message: MidiMessage, timeStamp: Long): Unit = {
        midiDevice.foreach(_.getReceiver.send(message, timeStamp))
        midiSequencer.foreach(_.getReceiver.send(message, timeStamp))
      }
      override def close(): Unit = {}
    }

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
        midiSequencer = Some(MidiSystem.getSequencer())
        midiSequence = Some(new Sequence(Sequence.PPQ, options.timing.pulsesPerQuarterNote, 16))
        device.open()
        for (midiSequencer <- midiSequencer; midiSequence <- midiSequence) {
          midiSequencer.open()
          midiSequencer.setSequence(midiSequence)
          midiSequencer.setTickPosition(0)
          midiSequencer.setTempoInBPM(120)
          for (idx <- 0 until 16) {
            val track = midiSequence.getTracks()(idx)
            val message = new MetaMessage()

            midiSequencer.recordEnable(track, idx)
          }
          midiSequencer.startRecording()
        }
      case None =>
        throw new RuntimeException("No MIDI devices available")
    }
  }

  def stop(): Unit = {
    for (midiDevice <- midiDevice) {
      midiDevice.close()
    }
    midiDevice = None

    for (midiSequencer <- midiSequencer; midiSequence <- midiSequence) {
      midiSequencer.stopRecording()
      val stream = new FileOutputStream("C:\\temp\\out.mid")
      MidiSystem.write(midiSequence, 1, stream)
      stream.close()
    }

    for (midiSequencer <- midiSequencer) {
      midiSequencer.stop()
      midiSequencer.close()
    }
    midiSequencer = None

    midiSequence = None
  }

  def device: Option[MidiDevice] = midiDevice
  def timing: Timing = options.timing
}

object MidiEngine {
  case class Options(preferredDevices: Seq[String] = Seq(), timing: Timing = Timing())
}

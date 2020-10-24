package seqlab.cli

import java.util.concurrent.Flow

import akka.actor.ActorSystem
import akka.stream.scaladsl.{JavaFlowSupport, Sink}
import javax.sound.midi.{MidiSystem, ShortMessage}
import seqlab.core.{ScheduledEvent, Sequencer}

import scala.concurrent.duration.DurationDouble

object Main {
  implicit val mainActorSystem: ActorSystem = ActorSystem()

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

      val options = new Sequencer.Options {
        override val actorSystem: ActorSystem = mainActorSystem
      }
      val sequencer = new Sequencer[Int](options)

      sequencer.schedule(List(
        ScheduledEvent(1.seconds.toNanos, 123),
        ScheduledEvent(1.5.seconds.toNanos, 456),
        ScheduledEvent(1.55.seconds.toNanos, 666),
        ScheduledEvent(1.9.seconds.toNanos, 900),
        ScheduledEvent(2.5.seconds.toNanos, 1000)
      ))

      val eventPublisher = sequencer.start()
      JavaFlowSupport.Source
        .fromPublisher(eventPublisher)
        .to(Sink.foreach(data => {
          println(data)
          if (data == 1000) {
            sequencer.stop()
            mainActorSystem.terminate()
          }
        }))
        .run()
    } finally {
      device.close()
    }
  }
}

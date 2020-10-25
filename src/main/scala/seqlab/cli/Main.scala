package seqlab.cli

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import javax.sound.midi.MidiSystem
import seqlab.core.actors.Sequencer
import seqlab.program.{Instruction, Program}
import seqlab.program.actors.ProgramExecutor
import seqlab.program.instructions.DebugPrint

import scala.concurrent.duration.DurationDouble

object Main {
  object RootActor {
    trait Message
    case object Start extends Message

    def apply(): Behavior[Message] =
      Behaviors.setup { context =>
        val sequencer = context.spawn(Sequencer[Instruction](), "sequencer")
        val executor = context.spawn(ProgramExecutor(sequencer), "executor")

        val outer = Program(
          DebugPrint(_ => "hello").at(0.3.seconds),
          DebugPrint(_ => "world").at(0.4.seconds)
        )
        val program = Program.repeat(outer, 1.second)
        sequencer ! Sequencer.AddEvents(program.instructions)

        Behaviors.receiveMessage {
          case Start =>
            sequencer ! Sequencer.Start(executor)
            Behaviors.ignore
        }
      }
  }

  implicit val mainActorSystem: ActorSystem[RootActor.Message] = ActorSystem(RootActor(), "rootActor")

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
      mainActorSystem ! RootActor.Start
    } finally {
      device.close()
    }
  }
}

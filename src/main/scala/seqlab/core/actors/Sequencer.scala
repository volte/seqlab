package seqlab.core.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import seqlab.core.ScheduledEvent

object Sequencer {
  sealed trait SequencerCommand[T]
  final case class AddEvents[T](events: ScheduledEvent[T]*) extends SequencerCommand[T]
  case class Start[T](client: ActorRef[ScheduledEvent[T]]) extends SequencerCommand[T]
  case class Stop[T]() extends SequencerCommand[T]

  def apply[T](): Behavior[SequencerCommand[T]] =
    Behaviors.setup { context =>
      val queue = context.spawn(EventQueue[T](), "eventQueue")
      val clock = context.spawn(EventDispatchClock(queue, 0), "eventDispatchClock")

      Behaviors.receiveMessage {
        case AddEvents(events @ _*) =>
          queue ! EventQueue.Enqueue(events)
          Behaviors.same
        case Start(client: ActorRef[ScheduledEvent[T]]) =>
          clock ! EventDispatchClock.Start(client)
          Behaviors.same
        case Stop() =>
          clock ! EventDispatchClock.Stop()
          Behaviors.same
      }
    }

  type SequencerActorRef[T] = ActorRef[Sequencer.SequencerCommand[T]]
}

package seqlab.core.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import seqlab.core.ScheduledEvent

object EventDispatchClock {
  sealed trait EventDispatchClockCommand[T]
  final case class Start[T](client: ActorRef[ScheduledEvent[T]]) extends EventDispatchClockCommand[T]
  final case class Stop[T]() extends EventDispatchClockCommand[T]
  private final case class Tick[T]() extends EventDispatchClockCommand[T]

  def apply[T](
      dispatcher: ActorRef[EventQueue.QueueCommand[T]],
      time: Long
  ): Behavior[EventDispatchClockCommand[T]] = stopped(dispatcher, time)

  private def stopped[T](
      dispatcher: ActorRef[EventQueue.QueueCommand[T]],
      time: Long
  ): Behavior[EventDispatchClockCommand[T]] =
    Behaviors.receive { (context, message) =>
      message match {
        case Start(client) =>
          context.self ! Tick()
          running(client, dispatcher, System.nanoTime(), time)
      }
    }

  private def running[T](
      client: ActorRef[ScheduledEvent[T]],
      dispatcher: ActorRef[EventQueue.QueueCommand[T]],
      offset: Long,
      time: Long
  ): Behavior[EventDispatchClockCommand[T]] =
    Behaviors.receive { (context, message) =>
      message match {
        case Tick() =>
          val delta = (System.nanoTime() - offset) - time
          val eventHandler = context.spawnAnonymous(handler(client))
          dispatcher ! EventQueue.Dequeue(time + delta, eventHandler)
          context.self ! Tick()
          running(client, dispatcher, offset, time + delta)
        case Stop() =>
          stopped(dispatcher, time)
      }
    }

  private def handler[T](
      client: ActorRef[ScheduledEvent[T]]
  ): Behavior[EventQueue.QueueEvent[T]] =
    Behaviors.receiveMessage {
      case EventQueue.EventsDue(events) =>
        for (event <- events) {
          client ! event
        }
        Behaviors.stopped
    }
}

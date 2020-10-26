package seqlab.core.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}
import seqlab.core.ScheduledEvent

import scala.concurrent.duration.DurationInt

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
          val eventHandler = context.spawnAnonymous(handler(client))
          Behaviors.withTimers { timers =>
            running(eventHandler, dispatcher, timers, System.nanoTime(), time)
          }
        case _ =>
          Behaviors.same
      }
    }

  private def running[T](
      eventHandler: ActorRef[EventQueue.QueueEvent[T]],
      dispatcher: ActorRef[EventQueue.QueueCommand[T]],
      timers: TimerScheduler[EventDispatchClockCommand[T]],
      offset: Long,
      time: Long
  ): Behavior[EventDispatchClockCommand[T]] =
    Behaviors.receive { (context, message) =>
      message match {
        case Tick() =>
          val delta = (System.nanoTime() - offset) - time
          dispatcher ! EventQueue.Dequeue(time + delta, eventHandler)
          timers.startSingleTimer(Tick(), 1.millis)
          running(eventHandler, dispatcher, timers, offset, time + delta)
        case Stop() =>
          context.stop(eventHandler)
          stopped(dispatcher, time)
        case _ =>
          Behaviors.same
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
        Behaviors.same
    }
}

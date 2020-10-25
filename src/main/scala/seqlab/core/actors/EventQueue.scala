package seqlab.core.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import seqlab.core.ScheduledEvent

object EventQueue {
  sealed trait QueueCommand[T]
  final case class Enqueue[T](newEvents: Seq[ScheduledEvent[T]]) extends QueueCommand[T]
  final case class Dequeue[T](upTo: Long, replyTo: ActorRef[QueueEvent[T]]) extends QueueCommand[T]

  sealed trait QueueEvent[T]
  final case class EventsDue[T](events: Seq[ScheduledEvent[T]]) extends QueueEvent[T]

  def apply[T](): Behavior[QueueCommand[T]] = dispatcher(Seq())

  private def mergeEvents[T](
      events: Seq[ScheduledEvent[T]]*
  ): Seq[ScheduledEvent[T]] =
    events.flatten.sorted

  private def dispatcher[T](
      events: Seq[ScheduledEvent[T]]
  ): Behavior[QueueCommand[T]] =
    Behaviors.receive { (context, message) =>
      message match {
        case Enqueue(newEvents) =>
          dispatcher(mergeEvents(events, newEvents))
        case Dequeue(upTo, replyTo) =>
          val (send, keep) = events.span(_.time < upTo)
          if (send.nonEmpty) {
            replyTo ! EventsDue(send)
            dispatcher(keep)
          } else {
            Behaviors.same
          }
      }
    }
}

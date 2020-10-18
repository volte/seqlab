package seqlab.core

import java.util.concurrent.Flow

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

/**
 * A playable that is only a single tick long and emits only a single data event.
 *
 * @param data The data the emit.
 */
case class SingletonPlayable[D](data: D) extends ForwardPlayable {
  case class SingletonCursor(context: Context, data: D) extends Cursor {
    implicit val actorSystem: ActorSystem = context.actorSystem

    private var done = false
    private val (eventQueue, eventPublisher) = Source
      .queue[PlayableEvent](3, OverflowStrategy.backpressure)
      .toMat(JavaFlowSupport.Sink.asPublisher[PlayableEvent](fanout = false))(Keep.both)
      .run()

    /** Advance the cursor by the given time span and return any events that have occurred in the time between
     * the old time and the new time. */
    override def advance(by: Long) {
      if (by <= 0) {
        return
      }
      if (!done) {
        done = true
        eventQueue.offer(PlayableEvent(0, PlayableMessage.Started))
        eventQueue.offer(PlayableEvent(0, PlayableMessage.Data(data)))
        eventQueue.offer(PlayableEvent(1, PlayableMessage.Finished))
        eventQueue.complete()
      }
    }

    /** The current time. */
    override def time: TimeOffset = if (done) { 1 } else { 0 }

    /** Source stream for this cursor. */
    override def events: Flow.Publisher[PlayableEvent] = eventPublisher
  }

  /** Return a cursor to the beginning of the sequence. */
  override def materialize(implicit context: Context): Cursor = SingletonCursor(context, data)
}

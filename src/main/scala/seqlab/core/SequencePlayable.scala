package seqlab.core

import java.util.concurrent.Flow

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

/**
 * A playable that is internally represented by a sequence of events.
 * The sequence of events must be in monotonically increasing order.
 */
class SequencePlayable[+D](private val seq: IndexedSeq[SequencePlayable.Data[D]]) extends ForwardPlayable {
  case class SequenceCursor(context: Context) extends Cursor {

    import context.actorSystem

    private val (eventQueue, eventPublisher) = Source
      .queue[PlayableEvent](128, OverflowStrategy.backpressure)
      .toMat(JavaFlowSupport.Sink.asPublisher[PlayableEvent](fanout = false))(Keep.both)
      .run()

    /** The duration of the sequence. */
    val duration: Long = seq.lastOption.map(_.time.offset + 1).getOrElse(0)

    private var index: Int = 0
    private var started: Boolean = false

    /** Advance the cursor by the given number of ticks. **/
    override def advance(by: Long) {
      if (by <= 0 || index >= seq.length) {
        return
      }
      if (!started) {
        started = true
        eventQueue.offer(PlayableEvent(0, PlayableMessage.Started))
      }

      var lastTime: Long = 0
      val targetOffset = _time + by
      while (index < seq.length && seq(index).time.offset < targetOffset.offset) {
        val data = seq(index)
        eventQueue.offer(PlayableEvent(data.time, PlayableMessage.Data(data.data)))
        index += 1
        lastTime = data.time.offset + 1
      }
      _time = math.min(targetOffset.offset, duration)
      if (index >= seq.length) {
        eventQueue.offer(PlayableEvent(lastTime, PlayableMessage.Finished))
        eventQueue.complete()
      }
    }

    /** The current time. */
    private var _time: TimeOffset = 0
    override def time: TimeOffset = _time

    /** Source stream for this cursor. */
    override def events: Flow.Publisher[PlayableEvent] = eventPublisher
  }

  /** Return a cursor to the beginning of the sequence. */
  override def materialize(implicit context: Context): Cursor = SequenceCursor(context)
}

object SequencePlayable {
  case class Data[+D](time: TimeOffset, data: D)

  def apply[D](seq: (TimeOffset, D)*): SequencePlayable[D] =
    new SequencePlayable(seq.map({ case (offset, data) => Data(offset.offset, data) }).toIndexedSeq)
}

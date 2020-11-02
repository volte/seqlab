package seqlab.core

import seqlab.core.Timeline.TimelineContext

/**
  * A mixer combines multiple timelines in parallel.
  */
class Mixer(init: Mixer.Context => Unit) extends Timeline {
  class Cursor extends Timeline.Cursor {
    private var cursors: Seq[Timeline#Cursor] = Seq()
    private var _time: TimePoint = 0

    init(Mixer.Context(Mixer.this, this))

    override def advance(span: TimeSpan): Boolean = {
      var curDone = true
      var maxDelta: TimeSpan = 0
      for (cursor <- cursors) {
        val prevTime = cursor.time
        curDone &= cursor.advance(span)
        val nextTime = cursor.time
        val delta = nextTime - prevTime
        if (delta > maxDelta) {
          maxDelta = delta
        }
      }
      _time += maxDelta
      cursors = cursors.filterNot(_.done)
      cursors.nonEmpty
    }

    override def abort(): Unit =
      cursors = Seq()

    def start(timeline: Timeline): Unit =
      cursors :+= timeline.instantiate()

    override def done: Boolean =
      cursors.isEmpty

    override def time: TimePoint =
      _time
  }

  override def instantiate(): Cursor =
    new Cursor()
}

object Mixer {
  type Init = Context => Unit
  case class Context(self: Mixer, cursor: Mixer#Cursor) extends TimelineContext[Mixer] {
    def channel(init: Channel.Context => Unit): Channel = {
      val newChannel = new Channel(init)
      cursor.start(newChannel)
      newChannel
    }
  }

  def apply(init: Init): Mixer =
    new Mixer(init)
}

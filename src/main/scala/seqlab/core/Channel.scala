package seqlab.core

class Channel(init: Channel.Init) extends Timeline {
  type Op = Channel.Context => Unit
  private var initialOps: Seq[Scheduled[Op]] = Seq()

  class Cursor extends Timeline.Cursor {
    private var _time: TimePoint = 0
    private var queue: ScheduledQueue[Op] = new ScheduledQueue[Op](initialOps: _*)

    init(Channel.Context(Channel.this, this))

    override def advance(span: TimeSpan): Boolean = {
      val events = queue.dequeue(span)
      for (event <- events) {
        val context = Channel.Context(Channel.this, this)
        event.data(context)
      }
      _time = queue.time
      !done
    }

    override def abort(): Unit =
      queue.clear()

    override def done: Boolean =
      queue.isEmpty

    override def time: TimePoint =
      _time

    def play(ops: Scheduled[Op]*): Unit = {
      _time = 0
      queue = new ScheduledQueue[Op](ops: _*)
    }
  }

  def play(ops: Scheduled[Op]*): Unit = {
    initialOps = ops
  }

  override def instantiate(): Cursor =
    new Cursor()
}

object Channel {
  type Init = Context => Unit

  case class Context(self: Channel, cursor: Channel#Cursor) extends Timeline.TimelineContext[Channel] {
    def play(ops: Scheduled[self.Op]*): Unit = {
      cursor.play(ops: _*)
    }
  }
}

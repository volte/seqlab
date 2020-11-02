package seqlab.core

class Sequence(init: Sequence.Init = Sequence.defaultInit) extends Timeline {
  class Cursor extends Timeline.Cursor {
    private var _time: TimePoint = 0
    private var queue: ScheduledQueue[Sequence.Op] = new ScheduledQueue[Sequence.Op]()

    init(Sequence.Context(Sequence.this, this))

    override def advance(span: TimeSpan): Boolean = {
      val events = queue.dequeue(span)
      for (event <- events) {
        val context = Sequence.Context(Sequence.this, this)
        event.data match {
          case Sequence.SimpleOp(fn)  => fn()
          case Sequence.ContextOp(fn) => fn(context)
        }
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

    def add(ops: Seq[Scheduled[Sequence.Op]]): Unit = {
      queue.enqueue(ops)
    }
  }

  override def instantiate(): Cursor =
    new Cursor()
}

object Sequence {
  sealed trait Op
  final case class SimpleOp(fn: () => Unit) extends Op
  final case class ContextOp(fn: Context => Unit) extends Op

  type Init = Context => Unit
  def defaultInit: Init = _ => {}

  def apply(init: Init = Sequence.defaultInit): Sequence =
    new Sequence(init)

  case class Context(self: Sequence, cursor: Sequence#Cursor) extends Timeline.TimelineContext[Sequence] {
    def add(time: TimePoint, op: Op): Unit = {
      cursor.add(Seq(Scheduled[Op](time, op)))
    }

    def add(ops: Scheduled[Op]*): Unit = {
      cursor.add(ops)
    }
  }

  implicit def fromSimpleFunction(fn: => Any): SimpleOp =
    SimpleOp({ () => fn })

  implicit def fromContextFunction(fn: Context => Any): ContextOp =
    ContextOp({ ctx => fn(ctx) })
}

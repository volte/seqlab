package seqlab.core

trait Receiver[T] {
  def receive(data: ScheduledEvent[T])
}

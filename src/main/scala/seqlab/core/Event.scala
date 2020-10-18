package seqlab.core

case class Event[+E, T <: TimeUnit](time: T#Point, data: E)(implicit val timeUnit: TimeUnit)

package seqlab.core

/**
 * An event emitted by a playable.
 * @param time The time at which the event occurred.
 * @param message The message contained in the event.
 */
case class PlayableEvent(time: TimeOffset, message: PlayableMessage)

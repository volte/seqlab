package seqlab.core

import akka.actor.ActorSystem

class Context {
  val actorSystem: ActorSystem = ActorSystem("SeqLabContext")
}

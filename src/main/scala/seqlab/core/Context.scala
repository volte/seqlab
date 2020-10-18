package seqlab.core

import akka.actor.ActorSystem

class Context {
  implicit val actorSystem: ActorSystem = ActorSystem("SeqLabContext")
}

package seqlab.core

import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should._

import scala.concurrent.duration.DurationInt

class SingletonPlayableSpec extends AnyFunSpec with Matchers {
  describe("SingletonPlayable") {
    implicit val context: Context = new Context()
    import context.actorSystem

    it("should complete its lifecycle") {
      val playable = SingletonPlayable(123)
      val cursor = playable.materialize

      val probe = JavaFlowSupport.Source
        .fromPublisher(cursor.events)
        .toMat(TestSink.probe[PlayableEvent])(Keep.right)
        .run()

      cursor.advance(100)

      probe
        .request(3)
        .expectNext(
          PlayableEvent(0, PlayableMessage.Started),
          PlayableEvent(0, PlayableMessage.Data(123)),
          PlayableEvent(1, PlayableMessage.Finished))
        .expectComplete()

      cursor.time.shouldEqual(TimeOffset(1))
    }
  }
}

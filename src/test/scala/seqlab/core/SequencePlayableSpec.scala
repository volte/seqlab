package seqlab.core

import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should._

import scala.concurrent.duration.DurationInt

class SequencePlayableSpec extends AnyFunSpec with Matchers {
  describe("SequencePlayable") {
    implicit val context: Context = new Context()
    import context.actorSystem

    it("should complete its lifecycle") {
      val playable = SequencePlayable(
        (0, "hello"),
        (2, "second message"),
        (2, "another message"),
        (15, "later"),
        (15, "later #2"),
        (20, "last message")
      )
      val cursor = playable.materialize

      val probe = JavaFlowSupport.Source
        .fromPublisher(cursor.events)
        .toMat(TestSink.probe[PlayableEvent])(Keep.right)
        .run()

      cursor.advance(1)
      probe.request(2).expectNext(
        PlayableEvent(0, PlayableMessage.Started),
        PlayableEvent(0, PlayableMessage.Data("hello")))

      cursor.advance(1)

      probe.request(2).expectNoMessage(0.millis)

      cursor.advance(10)

      probe
        .request(2)
        .expectNext(
          PlayableEvent(2, PlayableMessage.Data("second message")),
          PlayableEvent(2, PlayableMessage.Data("another message")))

      cursor.advance(6)

      probe.request(2)
        .expectNext(
          PlayableEvent(15, PlayableMessage.Data("later")),
          PlayableEvent(15, PlayableMessage.Data("later #2")))

      cursor.advance(10)

      probe.request(2)
        .expectNext(
          PlayableEvent(20, PlayableMessage.Data("last message")),
          PlayableEvent(21, PlayableMessage.Finished))

      cursor.time.shouldEqual(TimeOffset(21))
    }
  }
}

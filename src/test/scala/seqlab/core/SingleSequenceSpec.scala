package seqlab.core

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should._

class SingleSequenceSpec extends AnyFunSpec with Matchers {
  import seqlab.core.LongTimeUnit._

  describe("SingletonSequence") {
    val sequence = SingletonSequence(123)
    val cursor = sequence.begin

    it("new cursor should be NotStarted") {
      cursor.status.shouldEqual(SequenceStatus.NotStarted)
      val events = cursor.advance(1)
      events.length.shouldEqual(1)
      events.headOption.shouldEqual(Some(Event[Int, LongTimeUnit](0, 123)))
      cursor.status.shouldEqual(SequenceStatus.Finished)
    }
  }
}

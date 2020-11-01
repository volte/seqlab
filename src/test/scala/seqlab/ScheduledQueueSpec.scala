package seqlab

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import seqlab.core.ScheduledOps.ArrowOperator
import seqlab.core.{Scheduled, ScheduledQueue, TimePoint}

import scala.collection.mutable

class ScheduledQueueSpec extends AnyFunSpec with Matchers {
  import Matchers._

  describe("Schedule") {
    var received: Seq[Scheduled[String]] = Seq()

    val schedule1 = new ScheduledQueue(
      0 -->: "1.0", //
      10 -->: "1.10",
      20 -->: "1.20",
      21 -->: "1.21"
    )

    val schedule2 = new ScheduledQueue(
      0 -->: "2.0", //
      11 -->: "2.11",
      24 -->: "2.24",
      27 -->: "2.27"
    )

    val schedule3 = new ScheduledQueue(
      0 -->: "3.0", //
      10 -->: "3.10",
      31 -->: "3.31",
      44 -->: "3.44"
    )

    it("should parallelize") {
      val par = ScheduledQueue.parallel(schedule1, schedule2, schedule3)
      received = par.dequeue(0)
      received should contain theSameElementsInOrderAs Seq()
      received = par.dequeue(10)
      received should contain theSameElementsInOrderAs Seq( //
        0 -->: "1.0",
        0 -->: "2.0",
        0 -->: "3.0"
      )
      received = par.dequeue(1)
      received should contain theSameElementsInOrderAs Seq( //
        10 -->: "1.10",
        10 -->: "3.10"
      )
      received = par.dequeue(15)
      received should contain theSameElementsInOrderAs Seq( //
        11 -->: "2.11",
        20 -->: "1.20",
        21 -->: "1.21",
        24 -->: "2.24"
      )
      received = par.dequeue(25)
      received should contain theSameElementsInOrderAs Seq( //
        27 -->: "2.27",
        31 -->: "3.31",
        44 -->: "3.44"
      )
      received = par.dequeue(100)
      received should contain theSameElementsInOrderAs Seq()

      par should be(empty)
      par.time should equal(TimePoint(151))
    }
  }
}

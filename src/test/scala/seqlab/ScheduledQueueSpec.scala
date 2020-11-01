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
      val queue = ScheduledQueue.parallel(schedule1, schedule2, schedule3)
      queue.dequeue(0) should contain theSameElementsInOrderAs Seq()
      queue.time.ticks should be(0)
      queue.dequeue(10) should contain theSameElementsInOrderAs Seq( //
        0 -->: "1.0",
        0 -->: "2.0",
        0 -->: "3.0"
      )
      queue.time.ticks should be(10)
      queue.dequeue(1) should contain theSameElementsInOrderAs Seq( //
        10 -->: "1.10",
        10 -->: "3.10"
      )
      queue.time.ticks should be(11)
      queue.dequeue(15) should contain theSameElementsInOrderAs Seq( //
        11 -->: "2.11",
        20 -->: "1.20",
        21 -->: "1.21",
        24 -->: "2.24"
      )
      queue.time.ticks should be(26)
      queue.dequeue(25) should contain theSameElementsInOrderAs Seq( //
        27 -->: "2.27",
        31 -->: "3.31",
        44 -->: "3.44"
      )
      queue.time.ticks should be(51)
      queue.dequeue(100) should contain theSameElementsInOrderAs Seq()
      queue should be(empty)
      queue.time.ticks should be(151)
    }

    it("should sequence") {
      val queue = ScheduledQueue.sequence(schedule1, schedule2, schedule3)
      queue.dequeue(0) should contain theSameElementsInOrderAs Seq()
      queue.time.ticks should be(0)
      queue.dequeue(10) should contain theSameElementsInOrderAs Seq( //
        0 -->: "1.0"
      )
      queue.time.ticks should be(10)
      queue.dequeue(1) should contain theSameElementsInOrderAs Seq( //
        10 -->: "1.10"
      )
      queue.time.ticks should be(11)
      queue.dequeue(10) should contain theSameElementsInOrderAs Seq( //
        20 -->: "1.20"
      )
      queue.time.ticks should be(21)
      queue.dequeue(5) should contain theSameElementsInOrderAs Seq( //
        21 -->: "1.21",
        21 -->: "2.0"
      )
      queue.time.ticks should be(26)
      queue.dequeue(20) should contain theSameElementsInOrderAs Seq( //
        32 -->: "2.11",
        45 -->: "2.24"
      )
      queue.time.ticks should be(46)
      queue.dequeue(100) should contain theSameElementsInOrderAs Seq( //
        48 -->: "2.27",
        48 -->: "3.0",
        58 -->: "3.10",
        79 -->: "3.31",
        92 -->: "3.44"
      )
      queue.time.ticks should be(146)
      queue should be(empty)
    }
  }
}

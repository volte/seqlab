package seqlab.core

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should._

class TimeOffsetSpec extends AnyFunSpec with Matchers {
  describe("Ticks") {
    it ("should have arithmetic implemented correctly") {
      (TimeOffset(10) + 5) shouldEqual TimeOffset(15)
      (TimeOffset(15) - TimeOffset(10)) shouldEqual 5
    }
  }
}

package helpers

import java.time.LocalDateTime

import models.{Client, CpuLog}
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CpuHelperSpec extends Specification with Mockito with CpuHelper {

  "CpuHelper" should {

    val now = LocalDateTime.now
    val client = mock[Client]
    def toLog(tuple: (Int, Int)): CpuLog = CpuLog(42L, client, tuple._1, tuple._2, now)

    val simpleLogs = Seq((1, 10),(2, 20),(3, 30),(4, 40),(5, 50)).map(toLog)
    val allLogs = Seq((1, 20),(2, 25),(3, MISS),(4, MISS),(5, MISS),(6, 30),(7, MISS),(8, MISS),(9, 25),(10, 45)).map(toLog)

    "calculate a simple average" in {
      calculateCpuAverage(simpleLogs) must beEqualTo(30.0)
    }

    "calculate an average with included 'missing' values" in {
      calculateCpuAverage(allLogs) must beEqualTo(64.5)
    }

    "calculate an average with actual missing values calculated to be 100" in {
      calculateCpuAverage(allLogs.filterNot(_.missing)) must beEqualTo(64.5)
    }
  }
}

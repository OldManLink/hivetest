package repositories

import java.time.LocalDateTime

import models.{Client, CpuLog}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test._

@RunWith(classOf[JUnitRunner])
class CpuLogRepositorySpec extends Specification {

  "CpuLogRepository" should {

    var client: Client = null
    var cpuLogId: Long = -1L
    var cpuLogCount: Long = -1L

    "create a test client" in new WithApplication {
      client = app.injector.instanceOf[ClientRepository].create(Client(-1, "Foobar", LocalDateTime.now))
      client.isNew must beFalse
    }

    "create a single cpuLog" in new WithApplication {
      val created = app.injector.instanceOf[CpuLogRepository].create(CpuLog(-1, client, 42, 20, LocalDateTime.now))
      cpuLogId = created.id
      created.isNew must beFalse
    }

    "create five more cpuLogs" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      Seq(20, 30, 40, 50, 60).zip(Stream from 43).forall { case (percent, index) =>
        !repository.create(CpuLog(-1, client, index, percent, LocalDateTime.now)).isNew
      } must beTrue
    }

    "not retrieve a cpuLog that doesn't exist" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      repository.read(-42) must beNone
    }

    "retrieve the test cpuLog by id" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      val retrieved = repository.read(cpuLogId)
      retrieved.map(_.id) must beSome(cpuLogId)
      retrieved.map(_.client.id) must beSome(client.id)
    }

    "count the number of cpuLogs before deleting one" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      cpuLogCount = repository.count
      cpuLogCount must beGreaterThan(1L)
    }

    "delete the test cpuLog by id" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      repository.delete(cpuLogId) must beTrue
    }

    "count the number of cpuLog after deleting one" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      repository.count must beEqualTo(cpuLogCount - 1)
    }

    "read all cpuLogs for a client" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      val allLogs = repository.readLogsForClient(client.id)
      allLogs.length must beEqualTo(5)
      allLogs.forall(c => c.client.id == client.id)
      allLogs.map(c => c.percent) must beEqualTo(Seq(20, 30, 40, 50, 60))
    }

    "delete the test client" in new WithApplication {
      app.injector.instanceOf[ClientRepository].delete(client.id) must beTrue
    }

    "count the number of cpuLogs after deleting five" in new WithApplication {
      val repository = app.injector.instanceOf[CpuLogRepository]
      repository.count must beEqualTo(cpuLogCount - 6)
    }
  }
}

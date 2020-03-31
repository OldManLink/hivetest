package services

import java.time.LocalDateTime

import models.{Client, ClientCpuResponse, ClientResponse, CpuLog, CpuReport, LogResponse}
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import repositories.{ClientRepository, CpuLogRepository}

@RunWith(classOf[JUnitRunner])
class ClientCpuServiceSpec extends Specification with Mockito {

  "ClientCpuService" should {
    val now = LocalDateTime.now

    "create a client" in {
      val newClient = Client(42, "Foobar", now)
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.create(any[Client]()) returns(newClient)
      val cpuService = new ClientCpuServiceImpl(clientRepoMock, mock[CpuLogRepository])

      val newClientId = cpuService.createClient("Foobar")

      newClientId must beEqualTo(ClientResponse(42))
      there was one(clientRepoMock).create(any[Client]())
    }

    "log a cpu report" in {
      val client = mock[Client]
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.read(anyLong) returns(Some(client))
      val newCpuLog = CpuLog(42, client, 37, 42, now)
      val cpuLogRepositoryMock = mock[CpuLogRepository]
      cpuLogRepositoryMock.create(any[CpuLog]()) returns(newCpuLog)
      val cpuService = new ClientCpuServiceImpl(clientRepoMock, cpuLogRepositoryMock)

      val reportCpu = cpuService.reportCpu(CpuReport(42, 37, 73))

      reportCpu must beSome(LogResponse(now))
      there was one(clientRepoMock).read(42)
      there was one(cpuLogRepositoryMock).create(any[CpuLog]())
    }

    "fail to log a cpu report from a non-existent client" in {
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.read(anyLong) returns(None)
      val cpuLogRepositoryMock = mock[CpuLogRepository]
      val cpuService = new ClientCpuServiceImpl(clientRepoMock, cpuLogRepositoryMock)

      val reportCpu = cpuService.reportCpu(CpuReport(42, 37, 73))

      reportCpu must beNone
      there were noCallsTo(cpuLogRepositoryMock)
    }

    "return a cpu average" in {
      val client = mock[Client]
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.read(anyLong) returns(Some(client))
      val cpuLogRepositoryMock = mock[CpuLogRepository]
      cpuLogRepositoryMock.readLogsForClient(anyLong) returns Seq(
        CpuLog(42, client, 1, 10, now),
        CpuLog(42, client, 2, 30, now)
      )
      val cpuService = new ClientCpuServiceImpl(clientRepoMock, cpuLogRepositoryMock)

      val clientResponse = cpuService.getCpuAverage(42)
      clientResponse must beSome(ClientCpuResponse(42, 20))

      there was one(clientRepoMock).read(42)
      there was one(cpuLogRepositoryMock).readLogsForClient(anyLong)
    }

    "fail to return a cpu average for a non-existent client" in {
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.read(anyLong) returns(None)
      val cpuLogRepositoryMock = mock[CpuLogRepository]
      val cpuService = new ClientCpuServiceImpl(clientRepoMock, cpuLogRepositoryMock)

      val clientResponse = cpuService.getCpuAverage(42)
      clientResponse must beNone

      there was one(clientRepoMock).read(42)
      there were noCallsTo(cpuLogRepositoryMock)
    }
  }
}

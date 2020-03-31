package services

import java.time.LocalDateTime

import com.google.inject.Inject
import helpers.CpuHelper
import models.{Client, ClientCpuResponse, ClientResponse, CpuLog, CpuReport, LogResponse}
import repositories.{ClientRepository, CpuLogRepository}

trait ClientCpuService {
  def createClient(name: String): ClientResponse
  def reportCpu(cpuReport: CpuReport): Option[LogResponse]
  def getCpuAverage(clientId: Long): Option[ClientCpuResponse]
}

class ClientCpuServiceImpl @Inject()(clientRepo: ClientRepository, cpuLogRepo: CpuLogRepository) extends ClientCpuService with CpuHelper {

  override def createClient(name: String): ClientResponse = {
    val newClient = clientRepo.create(Client(-1, name, LocalDateTime.now))
    ClientResponse(newClient.id)
  }

  override def reportCpu(cpuReport: CpuReport): Option[LogResponse] = {
    println(s"Report: $cpuReport")
    clientRepo.read(cpuReport.id).map { client =>
      val newLog = cpuLogRepo.create(CpuLog(-1, client, cpuReport.sequence, cpuReport.percent, LocalDateTime.now))
      LogResponse(newLog.creationInstant)
    }
  }

  override def getCpuAverage(clientId: Long): Option[ClientCpuResponse] = {
    println(s"getCpuAverage($clientId)")
    clientRepo.read(clientId).map { client =>
      val allLogs = cpuLogRepo.readLogsForClient(client.id)
      ClientCpuResponse(clientId, calculateCpuAverage(allLogs))
    }
  }
}

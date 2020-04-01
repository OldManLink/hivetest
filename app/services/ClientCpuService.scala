package services

import java.time.LocalDateTime

import com.google.inject.Inject
import helpers.CpuHelper
import models.{Client, ClientCpuResponse, ClientResponse, CpuLog, CpuReport, LogResponse}
import play.api.Logging
import play.api.libs.json.Json
import repositories.{ClientRepository, CpuLogRepository}

trait ClientCpuService {
  def createClient(name: String): ClientResponse
  def reportCpu(cpuReport: CpuReport): Option[LogResponse]
  def getCpuAverage(clientId: Long): Option[ClientCpuResponse]
}

class ClientCpuServiceImpl @Inject()(clientRepo: ClientRepository, cpuLogRepo: CpuLogRepository) extends ClientCpuService with CpuHelper with Logging {

  override def createClient(name: String): ClientResponse = {
    val newClient = clientRepo.create(Client(-1, name, LocalDateTime.now))
    logger.info(s"Created client: ${Json.toJson(newClient)}")
    ClientResponse(newClient.id)
  }

  override def reportCpu(cpuReport: CpuReport): Option[LogResponse] = {
    logger.info(s"Report: ${Json.toJson(cpuReport)}")
    clientRepo.read(cpuReport.id).map { client =>
      val newLog = cpuLogRepo.create(CpuLog(-1, client, cpuReport.sequence, cpuReport.percent, LocalDateTime.now))
      LogResponse(newLog.creationInstant)
    }
  }

  override def getCpuAverage(clientId: Long): Option[ClientCpuResponse] = {
    logger.info(s"Get Cpu Average for client($clientId)")
    clientRepo.read(clientId).map { client =>
      val allLogs = cpuLogRepo.readLogsForClient(client.id)
      ClientCpuResponse(clientId, calculateCpuAverage(allLogs))
    }
  }
}

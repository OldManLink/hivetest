package controllers

import java.time.LocalDateTime

import javax.inject._
import models.{Client, ClientResponse, CpuLog, CpuReport, LogResponse, Summary}
import play.api.libs.json.Json
import play.api.mvc._
import repositories.{ClientRepository, CpuLogRepository}

@Singleton
class HomeController @Inject()(cc: ControllerComponents, clientRepo: ClientRepository, cpuLogRepo: CpuLogRepository) extends AbstractController(cc) {

  def appSummary: Action[AnyContent] = Action {
    Ok(Json.toJson(Summary("Hive Streaming Client Tester!")))
  }

  def newClientId: Action[AnyContent] = Action {
    request =>
      val clientAgent = request.headers("User-Agent")
      val newClient = clientRepo.create(Client(-1, clientAgent, LocalDateTime.now))
      Ok(Json.toJson(ClientResponse(newClient.id)))
  }

  def reportCpu: Action[AnyContent] = Action {
    request =>
      val json = request.body.asJson.get
      val cpuReport = json.as[CpuReport]
      println(s"Report: $cpuReport")
      clientRepo.read(cpuReport.id).map { client =>
        val newLog = cpuLogRepo.create(CpuLog(-1, client, cpuReport.sequence, cpuReport.percent, LocalDateTime.now))
        Ok(Json.toJson(LogResponse(newLog.creationInstant)))
      }.getOrElse(BadRequest("Unknown client ID"))
  }
}

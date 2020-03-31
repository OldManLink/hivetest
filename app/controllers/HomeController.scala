package controllers

import javax.inject._
import models.{CpuReport, Summary}
import play.api.libs.json.Json
import play.api.mvc._
import services.ClientCpuService

@Singleton
class HomeController @Inject()(cc: ControllerComponents, cpuService: ClientCpuService) extends AbstractController(cc) {

  def appSummary: Action[AnyContent] = Action {
    Ok(Json.toJson(Summary("Hive Streaming Client Tester!")))
  }

  def newClientId: Action[AnyContent] = Action {
    request => Ok(Json.toJson(cpuService.createClient(request.headers("User-Agent"))))
  }

  def reportCpu: Action[AnyContent] = Action {
    request =>
      val cpuReport = request.body.asJson.get.as[CpuReport]
      cpuService.reportCpu(cpuReport)
        .map(logResponse => Ok(Json.toJson(logResponse)))
        .getOrElse(BadRequest("Unknown client ID"))
  }

  def getCpuAverage(clientId: Long): Action[AnyContent] = Action {
    Ok(Json.toJson(cpuService.getCpuAverage(clientId)))
  }
}

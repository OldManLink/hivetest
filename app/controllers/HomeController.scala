package controllers

import java.time.{Instant, LocalDateTime}

import javax.inject._
import models.{Client, CpuReport}
import play.api.libs.json.Json
import play.api.mvc._
import repositories.ClientRepository

@Singleton
class HomeController @Inject()(cc: ControllerComponents, clientRepo: ClientRepository) extends AbstractController(cc) {

  def appSummary: Action[AnyContent] = Action {
    Ok(Json.obj("content" -> "Hive Streaming Client Tester!"))
  }

  def newClientId: Action[AnyContent] = Action {
    val newClient = clientRepo.create(Client(-1, "Foobar", LocalDateTime.now))
    Ok(Json.obj("id" -> newClient.id))
  }

  def reportCpu: Action[AnyContent] = Action {
    request =>
      val json = request.body.asJson.get
      val cpuReport = json.as[CpuReport]
      println(s"Report: $cpuReport")
      val now = Instant.now.toString
    Ok(Json.obj("now" -> s"$now"))
  }
}

package controllers

import java.time.Instant

import javax.inject._
import models.CpuReport
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def appSummary: Action[AnyContent] = Action {
    Ok(Json.obj("content" -> "Hive Streaming Client Tester!"))
  }

  def newClientId: Action[AnyContent] = Action {
    val now = Instant.now.getNano / 1000000
    Ok(Json.obj("id" -> now))
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

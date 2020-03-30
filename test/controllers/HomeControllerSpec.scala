package controllers

import java.time.LocalDateTime

import models.{Client, ClientResponse, CpuLog, CpuReport, LogResponse, Summary}
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test._
import repositories.{ClientRepository, CpuLogRepository}

@RunWith(classOf[JUnitRunner])
class HomeControllerSpec extends Specification with Mockito {

  val titleJson: JsValue = Json.toJson(Summary("Hive Streaming Client Tester!"))

  "HomeController GET" should {

    "render the appSummary resource from a new instance of controller" in new WithApplication {
      val controller = new HomeController(stubControllerComponents(), mock[ClientRepository], mock[CpuLogRepository])
      val summary = controller.appSummary().apply(getRequest("/summary"))

      status(summary) must beEqualTo(OK)
      contentType(summary) must beSome("application/json")
      val resultJson = contentAsJson(summary)
      resultJson must beEqualTo(titleJson)
    }

    "render the appSummary resource from the application" in new WithApplication {
      val controller = app.injector.instanceOf[HomeController]
      val summary = controller.appSummary().apply(getRequest("/summary"))

      status(summary) must beEqualTo(OK)
      contentType(summary) must beSome("application/json")
      val resultJson = contentAsJson(summary)
      resultJson must beEqualTo(titleJson)
    }

    "render the appSummary resource from the router" in new WithApplication {
      val request = getRequest("/api/summary")
      val summary = route(app, request).get

      status(summary) must beEqualTo(OK)
      contentType(summary) must beSome("application/json")
      val resultJson = contentAsJson(summary)
      resultJson must beEqualTo(titleJson)
    }

    "create a new client when requested" in new WithApplication {
      val newClient = Client(42, "Foobar", LocalDateTime.now)
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.create(any[Client]()) returns(newClient)
      val controller = new HomeController(stubControllerComponents(), clientRepoMock, mock[CpuLogRepository])
      val newClientId = controller.newClientId().apply(getRequest("/newClientId"))

      status(newClientId) must beEqualTo(OK)
      contentType(newClientId) must beSome("application/json")
      val resultJson = contentAsJson(newClientId)
      resultJson must beEqualTo(Json.toJson(ClientResponse(42)))
    }

    "log a cpu report when requested" in new WithApplication {
      val newClient = Client(42, "Foobar", LocalDateTime.now)
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.read(42L) returns(Some(newClient))

      val now = LocalDateTime.now
      val newCpuLog = CpuLog(42, newClient, 37, 73, now)
      val cpuLogRepoMock = mock[CpuLogRepository]
      cpuLogRepoMock.create(any[CpuLog]()) returns(newCpuLog)

      val controller = new HomeController(stubControllerComponents(), clientRepoMock, cpuLogRepoMock)

      val reportCpu = controller.reportCpu().apply(
        postJsonRequest("/reportCpu").withJsonBody(Json.toJson(CpuReport(42, 37, 73)))
      )

      status(reportCpu) must beEqualTo(OK)
      contentType(reportCpu) must beSome("application/json")
      val resultJson = contentAsJson(reportCpu)
      resultJson must beEqualTo(Json.toJson(LogResponse(now)))
    }

    "fail to log a cpu report from a non-existent client" in new WithApplication {
      val clientRepoMock = mock[ClientRepository]
      clientRepoMock.read(anyLong) returns None
      val cpuLogRepoMock = mock[CpuLogRepository]
      val controller = new HomeController(stubControllerComponents(), clientRepoMock, cpuLogRepoMock)

      val reportCpu = controller.reportCpu().apply(
        postJsonRequest("/reportCpu").withJsonBody(Json.toJson(CpuReport(42, 37, 73)))
      )

      status(reportCpu) must beEqualTo(BAD_REQUEST)
      contentType(reportCpu) must beSome("text/plain")
      val result = contentAsString(reportCpu)
      result must beEqualTo("Unknown client ID")
      there were noCallsTo(cpuLogRepoMock)
    }
  }

  private def getRequest(uri: String): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, uri).withHeaders("User-Agent" -> "Foobar")

  private def postJsonRequest(uri: String): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, uri)
    .withHeaders("User-Agent" -> "Foobar", "Content-Type" -> "application/json")
}

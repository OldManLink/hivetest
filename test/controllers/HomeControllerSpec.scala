package controllers

import java.time.LocalDateTime

import models.{Client, ClientCpuResponse, ClientResponse, CpuLog, CpuReport, LogResponse, Summary}
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test._
import services.{ClientCpuService, ClientCpuServiceImpl}

@RunWith(classOf[JUnitRunner])
class HomeControllerSpec extends Specification with Mockito {

  val titleJson: JsValue = Json.toJson(Summary("Hive Streaming Client Tester!"))

  "HomeController GET" should {

    "render the appSummary resource from a new instance of controller" in new WithApplication {
      val controller = new HomeController(stubControllerComponents(), mock[ClientCpuService])
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

    "create a new client" in new WithApplication {
      val cpuService = mock[ClientCpuService]
      cpuService.createClient(anyString) returns ClientResponse(42)
      val controller = new HomeController(stubControllerComponents(), cpuService)
      val newClientResponse = controller.newClientId().apply(getRequest("/newClientId"))

      status(newClientResponse) must beEqualTo(OK)
      contentType(newClientResponse) must beSome("application/json")
      val resultJson = contentAsJson(newClientResponse)
      resultJson must beEqualTo(Json.toJson(ClientResponse(42)))
      there was one(cpuService).createClient("Foobar")
    }

    "log a cpu report" in new WithApplication {
      val now = LocalDateTime.now
      val cpuService = mock[ClientCpuService]
      cpuService.reportCpu(any[CpuReport]()).returns(Some(LogResponse(now)))
      val controller = new HomeController(stubControllerComponents(), cpuService)

      val reportCpu = controller.reportCpu().apply(
        postJsonRequest("/reportCpu").withJsonBody(Json.toJson(CpuReport(42, 37, 73)))
      )

      status(reportCpu) must beEqualTo(OK)
      contentType(reportCpu) must beSome("application/json")
      val resultJson = contentAsJson(reportCpu)
      resultJson must beEqualTo(Json.toJson(LogResponse(now)))
      there was one(cpuService).reportCpu(CpuReport(42, 37, 73))
    }

    "fail to log a cpu report from a non-existent client" in new WithApplication {
      val cpuService = mock[ClientCpuService]
      cpuService.reportCpu(any[CpuReport]()).returns(None)
      val controller = new HomeController(stubControllerComponents(), cpuService)

      val reportCpu = controller.reportCpu().apply(
        postJsonRequest("/reportCpu").withJsonBody(Json.toJson(CpuReport(42, 37, 73)))
      )

      status(reportCpu) must beEqualTo(BAD_REQUEST)
      contentType(reportCpu) must beSome("text/plain")
      val result = contentAsString(reportCpu)
      result must beEqualTo("Unknown client ID")
    }


    "return a cpu average" in new WithApplication {
      val cpuService = mock[ClientCpuService]
      cpuService.getCpuAverage(anyLong).returns(Some(ClientCpuResponse(42, 75.5)))
      val controller = new HomeController(stubControllerComponents(), cpuService)

      val getCpuAverage = controller.getCpuAverage(42).apply(getRequest("/cpu/42"))

      status(getCpuAverage) must beEqualTo(OK)
      contentType(getCpuAverage) must beSome("application/json")
      val resultJson = contentAsJson(getCpuAverage)
      resultJson must beEqualTo(Json.toJson(ClientCpuResponse(42, 75.5)))
      there was one(cpuService).getCpuAverage(42)
    }

    "fail to return a cpu average from a non-existent client" in new WithApplication {
      val cpuService = mock[ClientCpuService]
      cpuService.getCpuAverage(anyLong).returns(None)
      val controller = new HomeController(stubControllerComponents(), cpuService)

      val getCpuAverage = controller.getCpuAverage(42).apply(getRequest("/cpu/42"))

      status(getCpuAverage) must beEqualTo(BAD_REQUEST)
      contentType(getCpuAverage) must beSome("text/plain")
      val result = contentAsString(getCpuAverage)
      result must beEqualTo("Unknown client ID")
    }

  }

  private def getRequest(uri: String): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, uri).withHeaders("User-Agent" -> "Foobar")

  private def postJsonRequest(uri: String): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, uri)
    .withHeaders("User-Agent" -> "Foobar", "Content-Type" -> "application/json")
}

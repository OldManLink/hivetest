package models

import play.api.libs.json.{Json, OFormat}

case class ClientCpuResponse(id: Long, cpuAverage: Double)

object ClientCpuResponse {
  implicit val clientCpuResponseFormat: OFormat[ClientCpuResponse] = Json.format[ClientCpuResponse]
}

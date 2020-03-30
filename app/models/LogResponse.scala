package models

import java.time.LocalDateTime

import play.api.libs.json.{Json, OFormat}

case class LogResponse(now: LocalDateTime)

object LogResponse {
  implicit val logResponseFormat: OFormat[LogResponse] = Json.format[LogResponse]
}

package models

import java.time.LocalDateTime

import play.api.libs.json.{Json, OFormat}

case class CpuLog(id: Long, client: Client, sequence: Long, percent: Int, creationInstant: LocalDateTime) extends PersistentObject

object CpuLog {
  implicit val cpuLogFormat: OFormat[CpuLog] = Json.format[CpuLog]
}

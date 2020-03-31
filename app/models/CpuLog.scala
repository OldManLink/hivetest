package models

import java.time.LocalDateTime

import helpers.CpuHelper
import play.api.libs.json.{Json, OFormat}

case class CpuLog(id: Long, client: Client, sequence: Long, percent: Int, creationInstant: LocalDateTime) extends PersistentObject with CpuHelper {
  def missing: Boolean = percent == MISS
}

object CpuLog {
  implicit val cpuLogFormat: OFormat[CpuLog] = Json.format[CpuLog]
}

package models

import play.api.libs.json.{Json, OFormat}

case class CpuReport(id: Long, sequence: Long, percent: Int)

object CpuReport {
  implicit val cpuReportFormat: OFormat[CpuReport] = Json.format[CpuReport]
}

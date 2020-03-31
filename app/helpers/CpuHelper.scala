package helpers

import models.CpuLog

trait CpuHelper {

  val MISS = 100

  def calculateCpuAverage(logs: Seq[CpuLog]): Double = {
    createMissingLogs(logs) match {
      case Nil => 0.0
      case allLogs => allLogs.foldLeft(0.0) { (sum: Double, log) => sum + log.percent } / allLogs.length
    }
  }

  private def createMissingLogs(logs: Seq[CpuLog]): Seq[CpuLog] = logs match {
    case Nil => logs
    case head :: _ =>
      (head.sequence to logs.last.sequence).map { sequence =>
        logs.find(log => log.sequence == sequence).getOrElse(head.copy(sequence = sequence, percent = MISS))
      }
  }
}

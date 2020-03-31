package repositories

import java.time.{Instant, LocalDateTime}
import java.time.ZoneOffset.UTC

import anorm.{RowParser, SqlParser, ~}
import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models.{Client, CpuLog}
import play.api.db.Database
import repositories.AnormInstantExtension._


trait CpuLogRepository {
  def create(cpuReport: CpuLog): CpuLog

  def read(id: Long): Option[CpuLog]

  def count: Long

  def delete(id: Long): Boolean

  def readLogsForClient(id: Long): Seq[CpuLog]
}

class CpuLogRepositoryImpl @Inject()(database: Database, clientRepo: ClientRepository) extends CpuLogRepository {


  val mapper: RowParser[CpuLog] = {
    get[Long]("id") ~
      get[Long]("client_id") ~
      get[Long]("sequence") ~
      get[Int]("cpu_percent") ~
      get[Instant]("creation_instant") map {
      case id ~ clientId ~ sequence ~ percent ~ creationInstant =>
        CpuLog(id, getClient(clientId), sequence, percent, LocalDateTime.ofInstant(creationInstant, UTC))
    }
  }

  private def getClient(id: Long): Client = clientRepo.read(id).get

  override def create(cpuLog: CpuLog): CpuLog = {
    database.withConnection { implicit c =>
      val result: Option[Long] =
        SQL("insert into cpu_log(client_id, sequence, cpu_percent, creation_instant) values ({clientId}, {sequence}, {percent}, {created})")
          .on("clientId" -> cpuLog.client.id,
            "sequence" -> cpuLog.sequence,
            "percent" -> cpuLog.percent,
            "created" -> cpuLog.creationInstant).executeInsert()
      result.map(resultingId => cpuLog.copy(id = resultingId)).get
    }
  }

  override def read(id: Long): Option[CpuLog] = {
    database.withConnection { implicit c =>
      SQL("SELECT * from cpu_log WHERE id={id}")
        .on("id" -> id).as(mapper *).headOption
    }
  }

  override def count: Long = {
    database.withConnection { implicit c =>
      SQL("SELECT COUNT(1) AS c from cpu_log").as(SqlParser.long("c").single)
    }
  }

  override def delete(id: Long): Boolean = {
    database.withConnection { implicit c =>
      SQL("DELETE from cpu_log WHERE id={id}")
        .on("id" -> id).executeUpdate() == 1
    }
  }

  override def readLogsForClient(clientId: Long): Seq[CpuLog] = {
    database.withConnection { implicit c =>
      SQL("SELECT * from cpu_log WHERE client_id={id}")
        .on("id" -> clientId).as(mapper *)
    }
  }
}
package repositories

import java.time.{Instant, LocalDateTime}
import java.time.ZoneOffset.UTC

import anorm.{RowParser, SqlParser, ~}
import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models.Client
import play.api.db.Database
import repositories.AnormInstantExtension._

trait ClientRepository {
  def create(client: Client): Client

  def read(id: Long): Option[Client]

  def count: Long

  def delete(id: Long): Boolean
}

class ClientRepositoryImpl @Inject()(database: Database) extends ClientRepository {


  val mapper: RowParser[Client] = {
    get[Long]("id") ~
      get[String]("name") ~
      get[Instant]("creation_instant") map {
      case id ~ name ~ creationInstant => Client(id, name, LocalDateTime.ofInstant(creationInstant, UTC))
    }
  }


  override def create(client: Client): Client = {
    database.withConnection { implicit c =>
      val result: Option[Long] =
        SQL("insert into client(name, creation_instant) values ({name}, {created})")
          .on("name" -> client.name,
          "created" -> client.creationInstant).executeInsert()
      result.map(resultingId => client.copy(id = resultingId)).get
    }
  }

  override def read(id: Long): Option[Client] = {
    database.withConnection { implicit c =>
      SQL("SELECT * from client WHERE id={id}")
        .on("id" -> id).as(mapper *).headOption
    }
  }

  override def count: Long = {
    database.withConnection { implicit c =>
      SQL("SELECT COUNT(1) AS c from client").as(SqlParser.long("c").single)
    }
  }

  override def delete(id: Long): Boolean = {
    database.withConnection { implicit c =>
      SQL("DELETE from client WHERE id={id}")
        .on("id" -> id).executeUpdate() == 1
    }
  }
}
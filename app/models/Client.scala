package models

import java.time.LocalDateTime

import play.api.libs.json.{Json, OFormat}

case class Client(id: Long, name: String, creationInstant: LocalDateTime) extends PersistentObject

object Client {
  implicit val clientFormat: OFormat[Client] = Json.format[Client]
}
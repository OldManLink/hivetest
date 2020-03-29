package models

import java.time.LocalDateTime

case class Client(id: Long, name: String, creationInstant: LocalDateTime) extends PersistentObject

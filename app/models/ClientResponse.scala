package models

import play.api.libs.json.{Json, OFormat}

case class ClientResponse(id: Long)

object ClientResponse {
  implicit val clientResponseFormat: OFormat[ClientResponse] = Json.format[ClientResponse]
}

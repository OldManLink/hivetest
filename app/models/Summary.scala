package models

import play.api.libs.json.{Json, OFormat}

case class Summary(content: String)

object Summary {
  implicit val summaryFormat: OFormat[Summary] = Json.format[Summary]
}

import java.time.Instant
import java.time.format.DateTimeFormatter

import anorm.{Column, MetaDataItem, ToStatement, TypeDoesNotMatch}

package object repositories {

  object AnormInstantExtension {
    val dateFormatGeneration: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss Z")

    implicit def rowToDateTime: Column[Instant] = Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match {
        case ts: java.sql.Timestamp => Right(ts.toInstant)
        case d: java.sql.Date => Right(d.toInstant)
        case str: java.lang.String => Right(Instant.from(dateFormatGeneration.parse(str)))
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass) )
      }
    }

    implicit val dateTimeToStatement: ToStatement[Instant] = new ToStatement[Instant] {
      def set(s: java.sql.PreparedStatement, index: Int, aValue: Instant): Unit = {
        if(aValue == null) {
          s.setTimestamp(index, null)
        } else {
          s.setTimestamp(index, java.sql.Timestamp.from(aValue) )
        }
      }
    }
  }
}
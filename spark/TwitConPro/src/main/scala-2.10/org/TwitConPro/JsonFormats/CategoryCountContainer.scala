package org.TwitConPro.JsonFormats

import java.time.{ZonedDateTime, format}

import spray.json.DefaultJsonProtocol._
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * Created by Gareth on 2016/06/11.
  */
case class CategoryCountContainer(Date: ZonedDateTime, Data: List[CategoryCount])
object CategoryCountContainer {
    implicit object DateJsonFormat extends RootJsonFormat[ZonedDateTime] {
        private val formatter: format.DateTimeFormatter = format.DateTimeFormatter.ISO_ZONED_DATE_TIME

        def write(value: ZonedDateTime) = JsString(value.format(formatter))

        def read(jsonValue: JsValue): ZonedDateTime = jsonValue match {
            case JsString(value) => ZonedDateTime.from(formatter.parse(value))
            case _ => throw new DeserializationException("Invalid Zoned Date Time provided")
        }
    }
    implicit val containerFormat = jsonFormat2(CategoryCountContainer.apply)
}

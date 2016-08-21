package org.TwitConPro.JsonFormats

import java.time.Instant

import spray.json.DefaultJsonProtocol._
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * Created by Gareth on 2016/06/11.
  */
case class CategoryCountContainer(Date: Instant, Data: List[CategoryCount])
object CategoryCountContainer {
    implicit object DateJsonFormat extends RootJsonFormat[Instant] {

        def write(value: Instant) = JsString(value.toString)

        def read(jsonValue: JsValue): Instant = jsonValue match {
            case JsString(value) => Instant.parse(value)
            case _ => throw DeserializationException("Invalid Instant provided")
        }
    }
    implicit val containerFormat = jsonFormat2(CategoryCountContainer.apply)
}

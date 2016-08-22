package org.TwitConPro.JsonProtocols

import java.time.Instant

import org.TwitConPro.JsonFormats._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * Created by Gareth on 2016/06/11.
  */
object TweetJsonProtocol extends DefaultJsonProtocol {

    implicit object DateJsonFormat extends RootJsonFormat[Instant] {
        def write(value: Instant) = JsString(value.toString)

        def read(jsonValue: JsValue): Instant = jsonValue match {
            case JsString(value) => Instant.parse(value).truncatedTo(java.time.temporal.ChronoUnit.HOURS)
            case _ => throw DeserializationException("Invalid Date Time provided")
        }
    }

    implicit val hashTagFormat = jsonFormat(HashTag, "hashTag")
    implicit val tweetFormat = jsonFormat(Tweet, "createdAt", "tweetText")
}

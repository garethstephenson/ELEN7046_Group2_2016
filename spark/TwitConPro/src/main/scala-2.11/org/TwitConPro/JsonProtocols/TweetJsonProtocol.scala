package org.TwitConPro.JsonProtocols

import java.time.{ZonedDateTime, format}

import org.TwitConPro.JsonFormats.{HashTag, Tweet, date}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * Created by Gareth on 2016/06/11.
  */
object TweetJsonProtocol extends DefaultJsonProtocol {

    implicit object DateJsonFormat extends RootJsonFormat[ZonedDateTime] {
        private val formatter: format.DateTimeFormatter = format.DateTimeFormatter.ISO_ZONED_DATE_TIME

        def write(value: ZonedDateTime) = JsString(value.format(formatter))

        def read(jsonValue: JsValue): ZonedDateTime = jsonValue match {
            case JsString(value) => ZonedDateTime.from(formatter.parse(value))
            case _ => throw new DeserializationException("Invalid Zoned Date Time provided")
        }
    }

    implicit val dateFormat = jsonFormat(date, "$date")
    implicit val hashTagFormat = jsonFormat(HashTag, "hashTag")
    implicit val tweetFormat = jsonFormat(Tweet, "_id", "createdBy", "createdAt", "coords", "favouriteCount", "hashtags",
        "twitterID", "inReplyToName", "inReplyToStatusID", "inReplyToUserID", "isRetweet", "language", "place", "sensitive",
        "quotedStatusID", "retweeted", "retweetedCount", "tweetText", "tweetURL")
}

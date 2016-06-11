package org.TwitConPro

import spray.json.DefaultJsonProtocol

/**
  * Created by Gareth on 2016/06/11.
  */
case class Tweet(_id: String,
                 createdBy: String,
                 createdAt: String,
                 coords: Array[Either[String, Double]],
                 favouriteCount: Int,
                 hashtags: Array[HashTag],
                 twitterID: String,
                 inReplyToName: String,
                 inReplyToStatusID: Int,
                 inReplyToUserID: Int,
                 isRetweet: Boolean,
                 language: String,
                 place: String,
                 sensitive: Boolean,
                 quotedStatusID: Int,
                 retweeted: Boolean,
                 retweetedCount: Int,
                 tweetText: String,
                 tweetURL: String)

object TweetJsonProtocol extends DefaultJsonProtocol {
  implicit val hashTagFormat = jsonFormat(HashTag, "hashTag")
  implicit val tweetFormat = jsonFormat(Tweet, "_id", "createdBy", "createdAt", "coords", "favouriteCount", "hashtags",
    "twitterID", "inReplyToName", "inReplyToStatusID", "inReplyToUserID", "isRetweet", "language", "place", "sensitive",
    "quotedStatusID", "retweeted", "retweetedCount", "tweetText", "tweetURL")
  /*  implicit object tweetFormat extends RootJsonFormat[Tweet] {
      def read(json: JsValue): Tweet = json match {
        case JsArray(Vector(JsString()))
      }
    }*/
}

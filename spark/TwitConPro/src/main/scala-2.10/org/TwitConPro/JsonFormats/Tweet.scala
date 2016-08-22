package org.TwitConPro.JsonFormats

import java.time.{Instant, ZonedDateTime}

/**
  * Created by Gareth on 2016/06/11.
  */
case class Tweet(//_id: Option[String],
                 //createdBy: String,
                 createdAt: Instant,
                 /*coords: Array[Either[String, Double]],
                 favouriteCount: Int,
                 hashtags: Array[Either[HashTag, String]],
                 twitterID: String,
                 inReplyToName: String,
                 inReplyToStatusID: Either[String, Int],
                 inReplyToUserID: Either[String, Int],
                 isRetweet: Boolean,
                 language: String,
                 place: String,
                 sensitive: Boolean,
                 quotedStatusID: Either[String, Int],
                 retweeted: Boolean,
                 retweetedCount: Int,*/
                 tweetText: String//,
                 //tweetURL: String
                )

package org.TwitConPro.JsonFormats

import java.time.ZonedDateTime

/**
  * Created by Gareth on 2016/06/11.
  */
case class Tweet(_id: String,
                 createdBy: String,
                 createdAt: ZonedDateTime,
                 coords: Array[Either[String, Double]],
                 favouriteCount: Int,
                 hashtags: Array[HashTag],
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
                 retweetedCount: Int,
                 tweetText: String,
                 tweetURL: String)

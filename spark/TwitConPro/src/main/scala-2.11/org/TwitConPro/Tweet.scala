package org.TwitConPro

/**
  * Created by Gareth on 2016/06/11.
  */
case class Tweet(_id: String, createdBy: String, createdAt: java.time.LocalDateTime, coords: Array[String], favoriteCount: Int,
    hashtags: Array[Object], twitterID: String, inReplyToName: String, inReplyToStatusID: Int, isRetweet: Boolean,
    language: String, place: String, sensitive: Boolean, quotedStatusID: Int, retweeted: Boolean, retweetedCount: Int,
    tweetText: String, tweetUrl: String)
package org.TwitConPro

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.TwitConPro.JsonFormats.Tweet
import org.TwitConPro.JsonProtocols.TweetJsonProtocol._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.spark.{SparkConf, SparkContext}
import spray.json._

/**
  * Created by Gareth on 2016/06/06.
  */
object CategoryCountPerHour {
    def main(args: Array[String]): Unit = {


        if (args.length < 1) {
            println("An invalid source path was supplied.")
            printUsage()
            return
        }
        val inputPath: String = args(0)

        if (args.length < 2) {
            println("Categories must be supplied.")
            printUsage()
            return
        }
        //val categories: Array[String] = args(1).split(",")

        val sparkConfig = new SparkConf()
        sparkConfig.setAppName("Category Count Per Hour")

        val sparkContext = new SparkContext(sparkConfig)
        val categories = args(1).split(",")

        val tweets = sparkContext
            .textFile(inputPath)
            .map(_.parseJson.convertTo[Tweet])

        //import ZonedDateTimeSort._
        val dates = tweets
            .map(tweet => {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00'Z'")
                ZonedDateTime.parse(getDate(tweet).format(formatter))
            }) // Convert to same hour date
            //.sortBy(date => date)
            .distinct() // Reduce to unique hourly times
            .collect()

        dates.foreach(date => {

            val results = categories
                .map(category => {
                    tweets
                        .filter(tweet => tweet.tweetText.contains(category) && getDate(tweet).getHour == date.getHour) // For each hour
                        .map(tweet => (category, 1))
                        .reduceByKey(_ + _) // Returns (Category, CountPerHour)
                        .map(x => (date, (x._1, x._2)))
                        .collect()
                })

            println(s"\nDate:\t\t$date")
            results
                .foreach(arr => {
                    arr
                        .filter(tuple => tuple._1 == date)
                        .foreach(thing => println(s"Category:\t${thing._2._1}\nCount:\t\t${thing._2._2}\n"))
                })
        })

        sparkContext.stop()
    }

    def getDate(tweet: Tweet): ZonedDateTime = {
        tweet.createdAt
        /*if (tweet.createdAt.isLeft) {
            tweet.createdAt.left.get
        } else {
            Instant.ofEpochSecond(tweet.createdAt.right.get.$date).atZone(ZoneId.of("GMT"))
        }*/
    }

    def getHour(tweet: Tweet): Int = {
        getDate(tweet).getHour
    }

    private def printUsage(): Unit = {
        println("Usage: CategoryCountPerHour [path] [categories]")
        println("Eg: CategoryCountPerHour /path/to/file category1[,category2[,category3]]")
    }

    def merge(srcPath: String, dstPath: String): Unit = {
        val hadoopConfig = new Configuration()
        val hdfs = FileSystem.get(hadoopConfig)
        FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), false, hadoopConfig, null)
        FileUtil.fullyDelete(hdfs, new Path(srcPath))
    }
}

package org.TwitConPro

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.TwitConPro.JsonFormats.{CategoryCount, CategoryCountContainer, CategoryCountPerHourOutput, Tweet}
import org.TwitConPro.JsonProtocols.TweetJsonProtocol._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.spark.{SparkConf, SparkContext}
import spray.json._

import scala.collection.mutable

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
        val inputPath = args(0)

        if (args.length < 2) {
            println("Categories must be supplied.")
            printUsage()
            return
        }

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
                ZonedDateTime.parse(tweet.createdAt.format(formatter))
            }) // Convert to same hour date
            //.sortBy(date => date)
            .distinct() // Reduce to unique hourly times
            .collect()

        //val output = CategoryCountPerHourOutput(mutable.MutableList())
        val containers = mutable.MutableList[CategoryCountContainer]()
        dates.foreach(date => {

            val results = categories
                .map(category => {
                    tweets
                        .filter(tweet => tweet.tweetText.contains(category) && tweet.createdAt.getHour == date.getHour) // For each hour
                        .map(tweet => (category, 1))
                        .reduceByKey(_ + _) // Returns (Category, CountPerHour)
                        .map(x => (date, (x._1, x._2))) // Returns (Date, (Category, CountPerHour))
                        .collect()
                })

            val categoryCount: mutable.MutableList[CategoryCount] = mutable.MutableList()
            println(s"\nDate:\t\t$date")

            results.foreach(arr => {

                arr
                    .filter(tuple => tuple._1 == date)
                    .foreach(thing => {

                        categoryCount.+=(CategoryCount(thing._2._1, thing._2._2))

                        println(s"Category:\t${thing._2._1}\nCount:\t\t${thing._2._2}\n")
                    })
            })
            val container = CategoryCountContainer(date, categoryCount.toArray)
            containers.+=(container)
        })

        val output = CategoryCountPerHourOutput(containers.toArray)

        println(output)
        println(output.container.toJson)
        sparkContext.stop()
    }

    def getHour(tweet: Tweet): Int = {
        tweet.createdAt.getHour
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

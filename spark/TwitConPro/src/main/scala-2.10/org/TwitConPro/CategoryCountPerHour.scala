package org.TwitConPro

import java.io._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.TwitConPro.JsonFormats._
import org.TwitConPro.JsonProtocols.TweetJsonProtocol._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.spark.{SparkConf, SparkContext}
import spray.json._

import scala.collection.mutable.ListBuffer

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
        var lastIndexOfPathSeparator = inputPath.lastIndexOf("/")
        if (lastIndexOfPathSeparator == -1)
            lastIndexOfPathSeparator = 0
        else
            lastIndexOfPathSeparator += 1

        if (args.length < 2) {
            println("Categories must be supplied.")
            printUsage()
            return
        }
        val categories = args(1).split(",")

        val sparkConfig = new SparkConf()

        val fileName = inputPath.substring(lastIndexOfPathSeparator, inputPath.length)
        sparkConfig.setAppName(s"Category Count Per Hour [$fileName]")

        val sparkContext = new SparkContext(sparkConfig)
        sparkContext.defaultMinPartitions

        var numPartitions: Int = sparkContext.defaultMinPartitions
        if (args.length > 2) {
            numPartitions = args(2).toInt
        }

        printSettings(inputPath, categories, numPartitions)

        import ZonedDateTimeSort._
        val tweets = sparkContext
            .textFile(inputPath, numPartitions)
            .map(stripConstructors(Array("ObjectId", "ISODate", "NumberLong"), _))
            .map(_.parseJson.convertTo[Tweet])
            //.sortBy(tweets => tweets.createdAt)

        val YearMonthDayHourFormat: String = "yyyy-MM-dd'T'HH:00:00'Z'"
        val dates = tweets
            .map(tweet => {
                val formatter = DateTimeFormatter.ofPattern(YearMonthDayHourFormat) // Convert to same hour date
                ZonedDateTime.parse(tweet.createdAt.format(formatter))
            })
            .distinct // Reduce to unique hourly times
            .sortBy(date => date)
            .collect

        val containers: ListBuffer[CategoryCountContainer] = new ListBuffer[CategoryCountContainer]
        dates.foreach(date => {

            val tweetsByHour = tweets
                .filter(tweet => {
                    val formatter = DateTimeFormatter.ofPattern(YearMonthDayHourFormat) // Convert to same hour date
                    val parsedDate = ZonedDateTime.parse(tweet.createdAt.format(formatter))
                    date.equals(parsedDate)
                })
                //.collect

            val results = categories
                .map(category => tweetsByHour // For each hour
                    .filter(tweet => tweet.tweetText.contains(category))
                    .map(tweet => (category, 1))
                    .reduce((tuple1, tuple2) => (tuple1._1, tuple1._2 + tuple2._2)))

            val categoryCounts: ListBuffer[CategoryCount] = new ListBuffer[CategoryCount]
            categoryCounts.appendAll(results.map(result => new CategoryCount(result._1, result._2)))

            containers += new CategoryCountContainer(date, categoryCounts.toList)
        })
        sparkContext.stop

        val output = new CategoryCountPerIntervalOutput(containers.toList)
        writeToFile(output.container.toJson.toString, s"$inputPath.results.json")
    }

    def printSettings(inputPath: String, categories: Array[String], numPartitions: Int): Unit = {
        println("\nUsing settings:")
        println(s"\tInput path:\t$inputPath")
        println(s"\tCategories:\t${categories.mkString(", ")}")
        println(s"\tPartitions:\t$numPartitions\n")
    }

    def writeToFile(contents: String, fileName: String): Unit = {
        val printWriter = new PrintWriter(new File(fileName))
        printWriter.write(contents)
        printWriter.close()
    }

    def stripConstructors(initializers: Array[String], content: String): String = {
        val initializerArray = initializers.mkString("|")
        val pattern = s"((?:$initializerArray)\\((\\S*)\\))".r
        pattern.replaceAllIn(content, foundMatch => foundMatch.subgroups(1))
    }

    private def printUsage(): Unit = {
        println("Usage: CategoryCountPerHour [path] [categories]")
        println("Eg: CategoryCountPerHour /path/to/file category1[,category2[,category3]]")
    }

    def merge(srcPath: String, dstPath: String): Unit = {
        val hadoopConfig = new Configuration()
        val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConfig)
        FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), false, hadoopConfig, null)
        FileUtil.fullyDelete(hdfs, new Path(srcPath))
    }
}

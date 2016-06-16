package org.TwitConPro

import java.io._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.TwitConPro.JsonFormats.{CategoryCount, CategoryCountContainer, CategoryCountPerHourOutput, Tweet}
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

        if (args.length < 2) {
            println("Categories must be supplied.")
            printUsage()
            return
        }
        val categories = args(1).split(",")

        val sparkConfig = new SparkConf()
        sparkConfig.setAppName("Category Count Per Hour")

        val sparkContext = new SparkContext(sparkConfig)
        sparkContext.defaultMinPartitions

        var numPartitions: Int = sparkContext.defaultMinPartitions
        if (args.length > 2) {
            numPartitions = args(2).toInt
        }

        println("Using settings:")
        println(s"\tInput path:\t$inputPath")
        println(s"\tCategories:\t${categories.mkString(", ")}")
        println(s"\tPartitions:\t$numPartitions")

        val tweets = sparkContext
            .textFile(inputPath, numPartitions)
            .map(stripObjectInitializers(Array("ObjectId", "ISODate", "NumberLong"), _))
            .map(_.parseJson.convertTo[Tweet])

        import ZonedDateTimeSort._
        val dates = tweets
            .map(tweet => {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00'Z'") // Convert to same hour date
                ZonedDateTime.parse(tweet.createdAt.format(formatter))
            })
            .distinct // Reduce to unique hourly times
            .sortBy(date => date)
            .collect

        val containers: ListBuffer[CategoryCountContainer] = new ListBuffer[CategoryCountContainer]
        dates.foreach(date => {

            val results = categories
                .map(category => tweets
                    .filter(tweet => tweet.tweetText.contains(category))
                    .filter(tweet => getHour(tweet) == date.getHour) // For each hour
                    .map(tweet => (category, 1))
                    .reduceByKey(_ + _) // Returns tuple (Category, CountPerHour)
                    .map(categoryCountTuple => (date, categoryCountTuple._1, categoryCountTuple._2)) // Returns tuple (Date, Category, CountPerHour)
                    .collect)

            val categoryCounts: ListBuffer[CategoryCount] = new ListBuffer[CategoryCount]
            results.foreach(result => categoryCounts
                .appendAll(result
                    .filter(datedCategoryCountTuple => datedCategoryCountTuple._1 == date)
                    .map(datedCategoryCountTuple => CategoryCount(datedCategoryCountTuple._2, datedCategoryCountTuple._3))))

            containers += CategoryCountContainer(date, categoryCounts.toList)
        })
        sparkContext.stop

        val output = CategoryCountPerHourOutput(containers.toList)
        writeToFile(output.container.toJson.prettyPrint, s"$inputPath.results.json")
    }

    def writeToFile(contents: String, fileName: String): Unit = {
        val printWriter = new PrintWriter(new File(fileName))
        printWriter.write(contents)
        printWriter.close()
    }

    def stripObjectInitializers(initializers: Array[String], content: String): String = {
        val initializerArray = initializers.mkString("|")
        val pattern = ("""((?:""" + initializerArray + """)\((\S*)\))""").r
        pattern.replaceAllIn(content, foundMatch => foundMatch.subgroups(1))
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
        val hdfs = org.apache.hadoop.fs.FileSystem.get(hadoopConfig)
        FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), false, hadoopConfig, null)
        FileUtil.fullyDelete(hdfs, new Path(srcPath))
    }
}

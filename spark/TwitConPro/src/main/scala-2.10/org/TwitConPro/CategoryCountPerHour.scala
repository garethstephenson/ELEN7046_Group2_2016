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
import java.io._

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

        val tweets = sparkContext
            .textFile(inputPath)
            .map(_.parseJson.convertTo[Tweet])

        import ZonedDateTimeSort._
        val dates = tweets
            .map(tweet => {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00:00'Z'") // Convert to same hour date
                ZonedDateTime.parse(tweet.createdAt.format(formatter))
            })
            .distinct() // Reduce to unique hourly times
            .sortBy(date => date)
            .collect()

        val containers = mutable.MutableList[CategoryCountContainer]()
        dates.foreach(date => {

            val results = categories
                .map(category => {
                    tweets
                        .filter(tweet => tweet.tweetText.contains(category) && tweet.createdAt.getHour == date.getHour) // For each hour
                        .map(tweet => (category, 1))
                        .reduceByKey(_ + _) // Returns tuple (Category, CountPerHour)
                        .map(categoryCountTuple => (date, (categoryCountTuple._1, categoryCountTuple._2))) // Returns tuple (Date, (Category, CountPerHour))
                        .collect()
                })

            val categoryCounts: mutable.MutableList[CategoryCount] = mutable.MutableList()
            //println(s"\nDate:\t\t$date")

            results.foreach(result => {

                result
                    .filter(datedCategoryCountTuple => datedCategoryCountTuple._1 == date)
                    .foreach(datedCategoryCountTuple => {
                        val categoryCountTuple = datedCategoryCountTuple._2
                        categoryCounts.+=(CategoryCount(categoryCountTuple._1, categoryCountTuple._2))

                        //println(s"Category:\t${categoryCountTuple._1}\nCount:\t\t${categoryCountTuple._2}\n")
                    })
            })
            val container = CategoryCountContainer(date, categoryCounts.toList)
            containers.+=(container)
        })
        sparkContext.stop()

        val output = CategoryCountPerHourOutput(containers.toList)
        val pw = new PrintWriter(new File(inputPath + ".results.json"))
        pw.write(output.container.toJson.prettyPrint)
        pw.close
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

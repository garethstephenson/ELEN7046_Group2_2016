package org.TwitConPro

import java.io._

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

        var numPartitions: Int = sparkContext.defaultMinPartitions
        if (args.length > 2) {
            numPartitions = args(2).toInt
        }

        printSettings(inputPath, categories, numPartitions)

        import InstantDateTimeSort._

        val tweets = sparkContext
            .textFile(inputPath, numPartitions)
            .map(stripConstructors(Array("ObjectId", "ISODate", "NumberLong"), _))
            .map(_.parseJson.convertTo[Tweet])
            .map(tweet => (tweet.createdAt, tweet.tweetText))
            .flatMap(tuple => {
                categories.map(category => {
                    if (tuple._2.contains(category))
                        ((tuple._1, category), 1)
                    else
                        ((tuple._1, category), 0)
                })
            })
            .collect()

        val rddTweets = sparkContext
            .parallelize(tweets, numPartitions)
            .reduceByKey(_ + _)
            .map(x => (x._1._1, new CategoryCount(x._1._2, x._2)))

        val result = rddTweets
            .groupBy(x => x._1)
            .map(x => {
                val categoryCounts = new ListBuffer[CategoryCount]
                categoryCounts.appendAll(x._2.map(y => y._2))
                new CategoryCountContainer(x._1, categoryCounts.toList)
            })
            .sortBy(x => x.Date)
            .collect

        sparkContext.stop

        val output = new CategoryCountPerIntervalOutput(result.toList)
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

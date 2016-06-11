package org.TwitConPro

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.spark.{SparkConf, SparkContext}
import spray.json._
import TweetJsonProtocol._

/**
  * Created by Gareth on 2016/06/06.
  */
object CategoryCountPerHour {
  def main(args: Array[String]): Unit = {

    val sparkConfig = new SparkConf()
    sparkConfig.setAppName("Category Count Per Hour")

    val sparkContext = new SparkContext(sparkConfig)

    var inputPath = ""
    if (args.length > 0)
      inputPath = args(0)
    else {
      println("An invalid source path was supplied.")
      printUsage()
      return
    }

    var categories = Array[String]()
    if (args.length > 1) {
      categories = args(1).split(",")
      val catLen = categories.length
      println(s"\tFound $catLen categories")
    }
    else {
      println("Categories must be supplied.")
      printUsage()
      return
    }

    val tweetsAsText = sparkContext.textFile(inputPath)

    for (category <- categories) {

      println(s"\n\tProcessing category $category")

      val tweetsPerCategoryPerHour = tweetsAsText
        .map(_.parseJson.convertTo[Tweet])
        .filter(_.tweetText.contains(category))
        .map(tweet => (java.time.ZonedDateTime.parse(tweet.createdAt).getHour, 1))
        .reduceByKey(_ + _)

      val results = tweetsPerCategoryPerHour.collect()
      for (result <- results) {
        println(s"\n\t\tCategory:\t$category\n\t\tHour:\t\t${result._1}\n\t\tCount:\t\t${result._2}")
      }
    }

    sparkContext.stop()
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

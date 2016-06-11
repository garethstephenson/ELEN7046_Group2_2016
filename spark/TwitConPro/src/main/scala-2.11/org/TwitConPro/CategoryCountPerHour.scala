package org.TwitConPro

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.spark.{SparkConf, SparkContext}
import spray.json._

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

      println(s"\tProcessing category $category")

      import TweetJsonProtocol._

      val tweetsForCategory = tweetsAsText
        .map(_.parseJson.convertTo[Tweet])
        .filter(_.tweetText.contains(category))
      //.map(tweet => java.time.LocalDateTime.parse(tweet.createdAt))

      val countPerHour = tweetsForCategory
        .map(tweet => (java.time.ZonedDateTime.parse(tweet.createdAt).getHour, 1))
        .reduceByKey(_ + _)

      val results = countPerHour.collect()
      for (result <- results) {
        println(s"\t\tCategory: $category\n\t\tHour:\t${result._1}\n\t\tCount:\t${result._2}")
      }
      //println(s"\t$category: ${countPerHour.collect()}")

      //println(s"\t=============> $category: ${tweetsForCategory.count()}")
    }
    /*
            val sqlContext = new SQLContext(sparkContext)
            val tweets = sqlContext.read.json(inputPath)

            for (category <- categories) {
                println(s"\tWorking on category '$category'")

                tweets.registerTempTable("tweets")

                val categoryTweetHours = sqlContext.sql(s"SELECT COUNT(A.*) Count, HOUR(A.createdAt) Hour FROM tweets A " +
                  s"JOIN (SELECT DISTINCT HOUR(createdAt) Hour FROM tweets) B WHERE A.tweetText LIKE '%$category%' AND " +
                  s"HOUR(A.createdAt) = B.Hour GROUP BY HOUR(A.createdAt)")

                // Push results into a 'table' of |Category|Date|Hour|Count|
                categoryTweetHours.show()

    /*
                val count = sqlContext.sql(s"SELECT COUNT(*) FROM tweets WHERE tweetText LIKE '%$category%'")
                count.write.json(s"/data/$category-count.out")
                merge(s"/data/$category-count.out", s"/data/$category-count.json")
    */
            }
    */
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

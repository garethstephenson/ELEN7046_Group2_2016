package org.TwitConPro

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

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

    val categories = Set()
    if (args.length > 1)
      categories.++(args(1).split(","))
    else {
      println("Categories must be supplied.")
      printUsage()
      return
    }

    val broadcastCategories = sparkContext.broadcast(categories)

    val sqlContext = new SQLContext(sparkContext)
    val records = sqlContext.read.json(inputPath)

    

    sparkContext.stop()
  }

  private def printUsage(): Unit = {
    println("Usage: CountPerCategory [path] [categories]")
    println("Eg: CountPerCategory /path/to/file category1[,category2[,category3]]")
  }
}

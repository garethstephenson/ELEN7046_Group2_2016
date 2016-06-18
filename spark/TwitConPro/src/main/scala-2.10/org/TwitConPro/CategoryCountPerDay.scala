package org.TwitConPro

import org.TwitConPro.JsonFormats.{CategoryCount, CategoryCountContainer, CategoryCountPerHourInput, CategoryCountPerHourOutput}
import org.apache.spark.{SparkConf, SparkContext}
import spray.json._

import scala.collection.mutable.ListBuffer

/**
  * Created by Gareth on 2016/06/18.
  */
object CategoryCountPerDay {

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

        val fileName = inputPath.substring(lastIndexOfPathSeparator, inputPath.length)

        val sparkConfig = new SparkConf()
        sparkConfig.setAppName(s"Category Count Per Day [$fileName]")
        val sparkContext = new SparkContext(sparkConfig)

        val categoryCountsPerHourPerDay = sparkContext
            .textFile(inputPath)
            .map(_.parseJson.convertTo[CategoryCountPerHourInput])

        val dates = categoryCountsPerHourPerDay
            .map(categoryCountPerHourInput => categoryCountPerHourInput.container)
            .map(container => container.map(x => x.Date))
            .flatMap(x => x)
            .collect()

        val date = dates.head

        val data = categoryCountsPerHourPerDay
            .map(categoryCountPerHourInput => categoryCountPerHourInput.container)
            .map(container => container.map(x => x.Data).flatMap(x => x))
            .flatMap(x => x)
            .collect()

        val categories = data.map(x => x.Category).distinct

        val categoryCount: ListBuffer[CategoryCount] = new ListBuffer[CategoryCount]
        categories.foreach(category => {
            val count = data
                .filter(x => x.Category.equals(category))
                .map(x => x.Count)
                .sum

            categoryCount.+=(new CategoryCount(category, count))
        })

        val result = new CategoryCountContainer(date, categoryCount.toList)
        println(result.toJson.prettyPrint)

        sparkContext.stop()
    }

    def printUsage(): Unit = {
        println("Usage: CategoryCountPerDay [path]")
        println("Eg: CategoryCountPerDay /path/to/file")
    }
}

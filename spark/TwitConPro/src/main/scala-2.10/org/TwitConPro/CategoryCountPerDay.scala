package org.TwitConPro

import java.io.{File, PrintWriter}
import java.time.{Instant, ZonedDateTime}
import java.time.format.DateTimeFormatter

import org.TwitConPro.JsonFormats._
import org.TwitConPro.JsonProtocols.TweetJsonProtocol._
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

        var numPartitions: Int = sparkContext.defaultMinPartitions
        if (args.length > 1) {
            numPartitions = args(1).toInt
        }

        printSettings(inputPath, numPartitions)

        val categoryCountsPerHourPerDay = sparkContext
            .textFile(inputPath, numPartitions)
            .map(_.parseJson.convertTo[CategoryCountPerIntervalInput])

        import InstantDateTimeSort._
        val YearMonthDayFormat: String = "yyyy-MM-dd'T'00:00:00'Z'"
        val dates = categoryCountsPerHourPerDay
            .map(categoryCountPerHourInput => categoryCountPerHourInput.container)
            .map(container => container.map(categoryCountContainer => {
                val formatter = DateTimeFormatter.ofPattern(YearMonthDayFormat)
                Instant.parse(categoryCountContainer.Date.formatted(formatter.toString))
            }))
            .flatMap(zonedDateTimes => zonedDateTimes)
            .distinct
            .sortBy(date => date)
            .collect()

        val containers: ListBuffer[CategoryCountContainer] = new ListBuffer[CategoryCountContainer]
        dates.foreach(date => {
            val data = categoryCountsPerHourPerDay
                .map(categoryCountPerHourInput => categoryCountPerHourInput.container)
                .map(containers => containers.filter(container => {
                    val formatter = DateTimeFormatter.ofPattern(YearMonthDayFormat)
                    val entryDate = ZonedDateTime.parse(container.Date.formatted(formatter.toString))
                    date.equals(entryDate)
                }))
                .map(containers => containers
                    .map(container => container.Data)
                    .flatMap(categoryCounts => categoryCounts))
                .flatMap(categoryCounts => categoryCounts)
                .collect()

            val categories = data
                .map(categoryCount => categoryCount.Category)
                .distinct

            val categoryCount: ListBuffer[CategoryCount] = new ListBuffer[CategoryCount]
            categories.foreach(category => {
                val count = data
                    .filter(categoryCount => categoryCount.Category.equals(category))
                    .map(categoryCount => categoryCount.Count)
                    .sum

                categoryCount += new CategoryCount(category, count)
            })
            val result = new CategoryCountContainer(date, categoryCount.toList)
            containers += result
        })

        sparkContext.stop()

        val output = new CategoryCountPerIntervalOutput(containers.toList)
        writeToFile(output.container.toJson.prettyPrint, s"$inputPath.categoryCountPerDay.json")
    }

    def printSettings(inputPath: String, numPartitions: Int): Unit = {
        println("\nUsing settings:")
        println(s"\tInput path:\t$inputPath")
        println(s"\tPartitions:\t$numPartitions\n")
    }

    def writeToFile(contents: String, fileName: String): Unit = {
        val printWriter = new PrintWriter(new File(fileName))
        printWriter.write(contents)
        printWriter.close()
    }

    def printUsage(): Unit = {
        println("Usage: CategoryCountPerDay [path]")
        println("Eg: CategoryCountPerDay /path/to/file")
    }
}

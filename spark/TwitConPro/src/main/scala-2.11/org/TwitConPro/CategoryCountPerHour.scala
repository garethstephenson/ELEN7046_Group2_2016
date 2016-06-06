package org.TwitConPro

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Gareth on 2016/06/06.
  */
object CategoryCountPerHour {
  def main(args: Array[String]): Unit = {

    val sparkConfig = new SparkConf()
    sparkConfig.setAppName("Category Count Per Hour")

    val sparkContext = new SparkContext(sparkConfig)



    sparkContext.stop()
  }
}

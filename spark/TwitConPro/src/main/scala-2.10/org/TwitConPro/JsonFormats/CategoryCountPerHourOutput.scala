package org.TwitConPro.JsonFormats

import spray.json.DefaultJsonProtocol._

/**
  * Created by Gareth on 2016/06/11.
  */
case class CategoryCountPerHourOutput(container: List[CategoryCountContainer])
object CategoryCountPerHourOutput {
    implicit val outputFormat = jsonFormat1(CategoryCountPerHourOutput.apply)
}

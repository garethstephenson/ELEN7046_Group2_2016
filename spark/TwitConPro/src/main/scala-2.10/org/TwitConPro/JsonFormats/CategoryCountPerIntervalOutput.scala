package org.TwitConPro.JsonFormats

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/**
  * Created by Gareth on 2016/06/11.
  */
case class CategoryCountPerIntervalOutput(container: List[CategoryCountContainer])
object CategoryCountPerIntervalOutput {
    implicit val outputFormat: RootJsonFormat[CategoryCountPerIntervalOutput] = jsonFormat1(CategoryCountPerIntervalOutput.apply)
}

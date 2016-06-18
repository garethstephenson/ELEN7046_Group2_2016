package org.TwitConPro.JsonFormats

import spray.json.DefaultJsonProtocol._

/**
  * Created by Gareth on 2016/06/11.
  */
case class CategoryCountPerIntervalOutput(container: List[CategoryCountContainer])
object CategoryCountPerIntervalOutput {
    implicit val outputFormat = jsonFormat1(CategoryCountPerIntervalOutput.apply)
}

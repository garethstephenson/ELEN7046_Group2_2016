package org.TwitConPro.JsonFormats

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/**
  * Created by Gareth on 2016/06/11.
  */
case class CategoryCount(Category: String, Count: Int)
object CategoryCount {
    implicit val categoryCountFormat: RootJsonFormat[CategoryCount] = jsonFormat2(CategoryCount.apply)
}

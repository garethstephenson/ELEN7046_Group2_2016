package org.TwitConPro.JsonFormats

import spray.json.DefaultJsonProtocol._

/**
  * Created by Gareth on 2016/06/11.
  */
case class CategoryCount(Category: String, Count: Int)
object CategoryCount {
    implicit val categoryCountFormat = jsonFormat2(CategoryCount.apply)
}

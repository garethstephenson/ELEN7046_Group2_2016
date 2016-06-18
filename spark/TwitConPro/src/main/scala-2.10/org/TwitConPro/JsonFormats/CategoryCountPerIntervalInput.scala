package org.TwitConPro.JsonFormats

import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.json.{JsValue, RootJsonFormat}

/**
  * Created by Gareth on 2016/06/18.
  */
case class CategoryCountPerIntervalInput(container: List[CategoryCountContainer])
object CategoryCountPerIntervalInput {
    implicit object ContainerFormat extends RootJsonFormat[CategoryCountPerIntervalInput] {
        def read(value: JsValue) = CategoryCountPerIntervalInput(value.convertTo[List[CategoryCountContainer]])
        def write(obj: CategoryCountPerIntervalInput) = obj.container.toJson
    }
}
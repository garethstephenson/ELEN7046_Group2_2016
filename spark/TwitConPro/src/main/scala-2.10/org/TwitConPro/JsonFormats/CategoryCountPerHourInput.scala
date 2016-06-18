package org.TwitConPro.JsonFormats

import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.json.{JsValue, RootJsonFormat}

/**
  * Created by Gareth on 2016/06/18.
  */
case class CategoryCountPerHourInput(container: List[CategoryCountContainer])
object CategoryCountPerHourInput {
    implicit object ContainerFormat extends RootJsonFormat[CategoryCountPerHourInput] {
        def read(value: JsValue) = CategoryCountPerHourInput(value.convertTo[List[CategoryCountContainer]])
        def write(obj: CategoryCountPerHourInput) = obj.container.toJson
    }
}
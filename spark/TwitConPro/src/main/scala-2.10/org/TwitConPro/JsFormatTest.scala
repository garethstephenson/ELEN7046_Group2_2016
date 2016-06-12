package org.TwitConPro

/**
  * Created by Gareth on 2016/06/12.
  */

import spray.json._

case class Color(name: String, red: Int, green: Int, blue: Int)

case class Team(name: String, color: Option[Color])

object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat(Color, "name", "r", "g", "b")
    implicit val teamFormat = jsonFormat(Team, "name", "jersey")
}

import MyJsonProtocol._


object JsFormatTest {
    def main(args: Array[String]): Unit = {

        val obj = Team("Red Sox", Some(Color("Red", 255, 0, 0)))
        val ast = obj.toJson
        println(obj)
        println(ast.prettyPrint)
        println(ast.convertTo[Team])
        println("""{ "name": "Red Sox", "jersey": null }""".asJson.convertTo[Team])
        println("""{ "name": "Red Sox" }""".asJson.convertTo[Team])

        /*val catCount = TestClass("gareth", 1)
        println(catCount)
        println(catCount.toJson.asJsObject.prettyPrint)*/
    }
}

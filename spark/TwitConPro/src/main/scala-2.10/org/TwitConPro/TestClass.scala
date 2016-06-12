package org.TwitConPro

import spray.json.{DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat, SerializationException}

/**
  * Created by Gareth on 2016/06/12.
  */
case class TestClass(name: String, age: Int)

object TestClass {

    //implicit val TestClassJson = jsonFormat2(TestClass.apply)
    implicit object TestClassFormat extends RootJsonFormat[TestClass] {
        override def write(obj: TestClass): JsValue = obj match {
            case s: TestClass => JsObject(("name", JsString(s.name)), ("age", JsNumber(s.age)))
            case _ => throw new SerializationException("Could not serialize object")
        }

        override def read(json: JsValue): TestClass = json match {
            case JsString(json) => TestClass("", 0)
            case _ => throw new DeserializationException("Could not deserialize object")
        }
    }
}

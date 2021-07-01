package study.softserve.scala

import weather.WeatherReply

import scala.collection.mutable.ListBuffer

object WeatherDatasource {
  var weathers: ListBuffer[WeatherReply] = new ListBuffer[WeatherReply]()
}

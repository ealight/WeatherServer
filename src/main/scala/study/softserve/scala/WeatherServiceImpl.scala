package study.softserve.scala

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.google.protobuf.empty.Empty
import com.redis.serialization.Parse
import weather.{WeatherReply, WeatherRequestByName, WeatherService}

import scala.concurrent.Future

class WeatherServiceImpl() extends WeatherService {
  implicit val parseWeatherReply: Parse[Array[Byte]] = Parse[Array[Byte]](x => x)

  override def getWeathersByCityName(in: WeatherRequestByName): Source[WeatherReply, NotUsed] = {
    val weatherList = WeatherDatasource.weathers.toList

    Source(weatherList
      .filter(_.name == in.name))
  }

  override def getAllWeathers(in: Empty): Source[WeatherReply, NotUsed] = {
    val weatherList = WeatherDatasource.weathers.toList
    Source(weatherList)
  }

  override def getWeatherByCityName(in: WeatherRequestByName): Future[WeatherReply] = {
    val weatherList = WeatherDatasource.weathers.toList
    Future.successful(weatherList.filter(_.name == in.name).head)
  }
}

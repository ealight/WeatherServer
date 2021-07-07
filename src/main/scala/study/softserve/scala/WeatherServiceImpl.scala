package study.softserve.scala

import com.redis.serialization.Parse
import weather.{WeatherReply, WeatherRequestByName, WeatherService}

import scala.concurrent.Future

class WeatherServiceImpl() extends WeatherService {
  implicit val parseWeatherReply: Parse[WeatherReply] = Parse[WeatherReply](x => WeatherReply.parseFrom(x))

  override def getWeatherByCityName(in: WeatherRequestByName): Future[WeatherReply] = {
    val weather = dbClient.get[WeatherReply](in.name)

    Future.successful(weather.get)
  }
}

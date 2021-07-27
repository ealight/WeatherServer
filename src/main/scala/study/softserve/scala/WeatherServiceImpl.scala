package study.softserve.scala

import com.redis.serialization.Parse
import org.slf4j.{Logger, LoggerFactory}
import weather.{WeatherReply, WeatherRequestByName, WeatherService}

import scala.concurrent.Future

class WeatherServiceImpl() extends WeatherService {
  implicit val parseWeatherReply: Parse[WeatherReply] = Parse[WeatherReply](x => WeatherReply.parseFrom(x))
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  override def getWeatherByCityName(in: WeatherRequestByName): Future[WeatherReply] = {
    val weather = dbClient.get[WeatherReply](in.name)

    log.info("Get weather by city name: {}", weather.get)
    Future.successful(weather.get)
  }
}

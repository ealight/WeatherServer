package study.softserve.scala

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.slf4j.LoggerFactory
import weather.WeatherServiceHandler

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object WeatherServer extends App {
  new WeatherServer().run()
}

class WeatherServer() {
  def run(): Unit = {
    implicit val ec: ExecutionContext = system.dispatcher
    val log = LoggerFactory.getLogger(this.getClass)

    val serverConfig = config.getConfig("application.server")

    val host = serverConfig.getString("host")
    val port = serverConfig.getInt("port")

    val service: HttpRequest => Future[HttpResponse] =
      WeatherServiceHandler.withServerReflection(new WeatherServiceImpl())

    val binding = Http()
      .newServerAt(host, port)
      .bind(service)

    binding.onComplete {
      case Success(value) => log.info(s"gRPC successfully server bound to: ${value.localAddress}")
      case Failure(exception) => log.error(s"gRPC refused to start, reason: ${exception.getMessage}")
    }
  }
}


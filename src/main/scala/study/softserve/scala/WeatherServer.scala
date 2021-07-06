package study.softserve.scala

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.slf4j.LoggerFactory
import weather.WeatherServiceHandler

import scala.concurrent.{ExecutionContext, Future}

object WeatherServer extends App {
  val consumerSettings = ConsumerInitializer.setUp()

  ConsumerInitializer.run(consumerSettings)
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

    binding.foreach { binding => log.info(s"gRPC server bound to: ${binding.localAddress}") }
  }
}


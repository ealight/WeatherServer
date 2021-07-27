package study.softserve.scala

import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.slf4j.LoggerFactory
import weather.WeatherServiceHandler

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
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

    val password: Array[Char] = "dendi135".toCharArray // do not store passwords in code, read them from somewhere safe!

    val ks: KeyStore = KeyStore.getInstance("PKCS12")
    val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("server.p12")

    ks.load(keystore, password)

    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(ks, password)

    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    tmf.init(ks)

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
    val https: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)

    val service: HttpRequest => Future[HttpResponse] =
      WeatherServiceHandler.withServerReflection(new WeatherServiceImpl())

    val binding = Http()
      .newServerAt(host, port)
      .enableHttps(https)
      .bind(service)

    binding.onComplete {
      case Success(value) => log.info(s"gRPC successfully server bound to: ${value.localAddress}")
      case Failure(exception) => log.error(s"gRPC refused to start, reason: ${exception.getMessage}")
    }
  }
}


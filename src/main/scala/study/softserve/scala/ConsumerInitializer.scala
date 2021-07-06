package study.softserve.scala

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.slf4j.LoggerFactory
import weather.WeatherReply

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object ConsumerInitializer {
  def setUp(): ConsumerSettings[String, Array[Byte]] = {
    val consumerConfig = config.getConfig("akka.kafka.consumer")
    ConsumerSettings(consumerConfig, new StringDeserializer, new ByteArrayDeserializer)
  }

  def run(setting: ConsumerSettings[String, Array[Byte]]): Unit = {
    implicit val ec: ExecutionContext = system.dispatcher

    val log = LoggerFactory.getLogger(this.getClass)

    val akkaConfig = config.getConfig("application.akka.kafka")
    val topic = akkaConfig.getString("subscribe-topic")

    val consume = Consumer
      .plainSource(setting, Subscriptions.topics(topic))
      .runWith(
        Sink.foreach(x =>
          WeatherDatasource.weathers += WeatherReply.parseFrom(x.value()))
      )

    consume.onComplete {
      case Success(value) => {
        log.info(value.toString)
        system.terminate()
      }
      case Failure(exception) => log.error(exception.getMessage)
    }
  }
}

package study.softserve.scala

import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import weather.WeatherReply

import scala.Console.println
import scala.concurrent.ExecutionContext

object ConsumerInitializer {
  def setUp(): ConsumerSettings[String, Array[Byte]] = {
    val config = ConfigFactory.load()
    val consumerConfig = config.getConfig("akka.kafka.consumer")

    ConsumerSettings(consumerConfig, new StringDeserializer, new ByteArrayDeserializer)
  }

  def run(setting: ConsumerSettings[String, Array[Byte]]): Unit = {
    implicit val ec: ExecutionContext = system.dispatcher

    val akkaConfig = config.getConfig("application.akka.kafka")
    val topic = akkaConfig.getString("subscribe-topic")

    val consume = Consumer
      .plainSource(setting, Subscriptions.topics(topic))
      .runWith(
        Sink.foreach(x =>
          WeatherDatasource.weathers += WeatherReply.parseFrom(x.value()))
      )

    consume.onComplete(result => {
      println(result)
      system.terminate()
    })
  }
}

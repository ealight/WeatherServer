package study.softserve

import akka.actor.ActorSystem
import com.redis.RedisClient
import com.typesafe.config.{Config, ConfigFactory}

package object scala {
  implicit val system: ActorSystem = ActorSystem("Actor")
  val config: Config = ConfigFactory.load()
  val dbClient: RedisClient = DatasourceConnection.get()
}

package study.softserve.scala

import com.redis.RedisClient
import org.slf4j.LoggerFactory

object DatasourceConnection {
  def get(): RedisClient = {
    val log = LoggerFactory.getLogger(this.getClass)

    val dbConnection = config.getConfig("application.datasource.connection")
    val host = dbConnection.getString("host")
    val port = dbConnection.getInt("port")

    val redisClient = new RedisClient(host, port)

    try {
      redisClient.llen("testConnection")
    } catch {
      case e: Exception =>
        log.error(s"Can't connect to Redis, reason: ${e.getMessage}")
        System.exit(0)
    }

    log.info(s"Successfully connect to Redis Client. Host: $host, port: $port")

    redisClient
  }
}

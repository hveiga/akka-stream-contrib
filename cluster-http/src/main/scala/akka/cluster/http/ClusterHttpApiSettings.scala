package akka.cluster.http

import com.typesafe.config.Config

/**
  * Created by hecortiz on 9/17/16.
  */
final class ClusterHttpApiSettings(val config: Config) {
  private val cc = config.getConfig("akka.cluster.http-api")
  val httpApiPort = cc.getInt("port")
}

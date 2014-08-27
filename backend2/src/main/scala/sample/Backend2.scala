package sample

import java.net.InetAddress

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory

object Backend2 extends App{

  val address = InetAddress.getByName(System.getenv.get("HOSTNAME"))
  val hostAddress = address.getHostAddress
  println("\nHostAddress: " + hostAddress)

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + hostAddress).
    withFallback(ConfigFactory.load())

  // Create an Akka system
  val system = ActorSystem("ClusterSystem", config)
  system.actorOf(Props[TransformationBackend], name = "transformationBackend2")

}

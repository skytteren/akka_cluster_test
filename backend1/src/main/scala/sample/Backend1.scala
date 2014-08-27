package sample

import java.net.InetAddress

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.{ConfigValueFactory, ConfigFactory}

/**
 * Created by steskytt on 04.08.14.
 */
object Backend1 extends App{

  import scala.collection.JavaConversions._
  println("sysprops")
  System.getProperties.list(System.out)
  println("envs")
  System.getenv().toList.sortBy(_._1).foreach(println)

  val address = InetAddress.getByName(System.getenv.get("HOSTNAME"))
  val hostAddress = address.getHostAddress
  println("\nHostAddress: " + hostAddress)

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + hostAddress).
    withFallback(ConfigFactory.load())

  // Create an Akka system
  val system = ActorSystem("ClusterSystem", config)

  system.actorOf(Props[TransformationBackend], name = "transformationBackend1")

}

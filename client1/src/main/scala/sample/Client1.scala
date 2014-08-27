package sample

import java.net.InetAddress

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

object Client1 extends App{

  val address = InetAddress.getByName(System.getenv.get("HOSTNAME"))
  val hostAddress = address.getHostAddress
  println("\nHostAddress: " + hostAddress)

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + hostAddress).
    withFallback(ConfigFactory.load())

  // Create an Akka system
  val system = ActorSystem("ClusterSystem", config)
  val f1 = system.actorOf(Props[TransformationFrontend], name = "frontend")

  import system.dispatcher

  implicit val timeout = Timeout(10 seconds)

  var _i = 0

  def i: Int = {
    _i += 1
    _i
  }

  system.scheduler.schedule(10 seconds, 5 seconds)((f1 ? TransformationJob(s"tranform asfsadf $i")).foreach(r => println(s"result: $r")))
}

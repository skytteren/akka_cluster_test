package sample

import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

object Client2 extends App{

  val system = ActorSystem("ClusterSystem")
  val f1 = system.actorOf(Props[TransformationFrontend], name = "frontend")

  import system.dispatcher

  implicit val timeout = Timeout(10 seconds)

  var _i = 0

  def i: Int = {
    _i += 1
    _i
  }

  system.scheduler.schedule(10 seconds, 5 seconds)((f1 ? TransformationJob(s"2: tranform $i")).foreach(r => println(s"result: $r")))
}

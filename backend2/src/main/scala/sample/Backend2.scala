package sample

import akka.actor.{Props, ActorSystem}

object Backend2 extends App{

  val system = ActorSystem("ClusterSystem")
  system.actorOf(Props[TransformationBackend], name = "transformationBackend2")

}

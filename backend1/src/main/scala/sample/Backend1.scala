package sample

import akka.actor.{Props, ActorSystem}

/**
 * Created by steskytt on 04.08.14.
 */
object Backend1 extends App{

  val system = ActorSystem("ClusterSystem")
  system.actorOf(Props[TransformationBackend], name = "transformationBackend1")

}

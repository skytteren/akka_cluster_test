package sample

import akka.cluster._
import akka.cluster.ClusterEvent._
import akka.actor.{RootActorPath, ActorLogging, Actor}

case class TransformationJob(text: String)
case class TransformationResult(text: String)
case class JobFailed(reason: String, job: TransformationJob)
case object BackendRegistration

class TransformationBackend extends Actor with ActorLogging {
 
  val cluster = Cluster(context.system)
 
  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit =
    cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)
 
  def receive = {
    case TransformationJob(text) =>
      log.info(s"transform: $text")
      sender() ! TransformationResult(text.toUpperCase)
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
  }
 
  def register(member: Member): Unit =
    if (member.hasRole("frontend")) {
      log.info(s"register: $member")
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
        BackendRegistration
    }
}

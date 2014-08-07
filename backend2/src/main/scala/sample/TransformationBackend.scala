package sample

import akka.cluster._
import akka.cluster.ClusterEvent._
import akka.actor.{RootActorPath, ActorLogging, Actor}
import akka.persistence.{SnapshotOffer, PersistentActor}

case class TransformationJob(text: String)

case class TransformationResult(text: String)

case class JobFailed(reason: String, job: TransformationJob)

case object BackendRegistration

case class Cmd(data: String)
case class Evt(data: String, num: Int)

case class ExampleState(event: Evt) {
  def updated(evt: Evt): ExampleState = copy(event = evt)
}

class TransformationBackend extends PersistentActor with ActorLogging {

  val persistenceId = "backend2"

  val cluster = Cluster(context.system)

  var state = ExampleState(Evt("", 0))

  def updateState(event: Evt): Unit = {
    state = state.updated(event)
  }

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = {
    super.preStart()
    cluster.subscribe(self, classOf[MemberUp])
  }
  override def postStop(): Unit = {
    super.postStop()
    cluster.unsubscribe(self)
  }

  val receiveRecover: Receive = {
    case evt: Evt                                 => updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  def receiveCommand = {
    case TransformationJob(text) =>
      log.info(s"transform: $text")
      persist(Evt(text, state.event.num + 1)) { event =>
        updateState(event)
        sender() ! TransformationResult(event.data.toUpperCase + " ::: " + event.num)
        context.system.eventStream.publish(event)
      }
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

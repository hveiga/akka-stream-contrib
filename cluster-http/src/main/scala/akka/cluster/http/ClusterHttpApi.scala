package akka.cluster.http

import akka.actor.AddressFromURIString
import akka.cluster.{Cluster, Member}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.RouteResult
import akka.stream.ActorMaterializer
import spray.json._

/**
  * Created by hecortiz on 9/17/16.
  */

case class ClusterUnreachableMember(node: String, observedBy: Seq[String])

case class ClusterMember(node: String, nodeUid: String, status: String, roles: Set[String])

case class ClusterMembers(selfNode: String, members: Set[ClusterMember], unreachable: Seq[ClusterUnreachableMember])

case class ClusterHttpApiMessage(message: String)

trait ClusterHttpApiJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val clusterUnreachableMemberFormat = jsonFormat2(ClusterUnreachableMember)
  implicit val clusterMemberFormat = jsonFormat4(ClusterMember)
  implicit val clusterMembersFormat = jsonFormat3(ClusterMembers)
  implicit val clusterMemberMessageFormat = jsonFormat1(ClusterHttpApiMessage)
}

trait ClusterHttpApiHelper extends ClusterHttpApiJsonProtocol {
  def memberToClusterMember(m: Member): ClusterMember = {
    ClusterMember(s"${m.uniqueAddress.address}", s"${m.uniqueAddress.uid}", s"${m.status}", m.roles)
  }
}

object ClusterHttpRoute extends ClusterHttpApiHelper {

  def apply(cluster: Cluster) =
    path("members") {
      get {
        complete {
          val members = cluster.readView.state.members.map(memberToClusterMember)

          val unreachable = cluster.readView.reachability.observersGroupedByUnreachable.toSeq.sortBy(_._1).map {
            case (subject, observers) ⇒
              ClusterUnreachableMember(s"${subject.address}", observers.toSeq.sorted.map(m ⇒ s"${m.address}"))
          }

          ClusterMembers(s"${cluster.readView.selfAddress}", members, unreachable)
        }
      } ~
        post {
          formField('address) { address ⇒
            complete {
              cluster.join(AddressFromURIString(address))
              ClusterHttpApiMessage("Operation executed")
            }
          }
        }
    } ~
      path("members" / Segment) { member ⇒
        get {
          complete {
            cluster.readView.members.find(m ⇒ s"${m.uniqueAddress.address}" == member).map(memberToClusterMember)
          }
        } ~
          delete {
            cluster.readView.members.find(m ⇒ s"${m.uniqueAddress.address}" == member) match {
              case Some(m) =>
                cluster.leave(m.uniqueAddress.address)
                complete(ClusterHttpApiMessage("Operation executed"))
              case None =>
                complete(StatusCodes.NotFound -> ClusterHttpApiMessage("Member not found"))
            }
          } ~
          put {
            formField('operation) { operation =>
                cluster.readView.members.find(m ⇒ s"${m.uniqueAddress.address}" == member) match {
                  case Some(m) =>
                    operation.toUpperCase match {
                      case "DOWN" =>
                        cluster.down(m.uniqueAddress.address)
                        complete(ClusterHttpApiMessage("Operation executed"))
                      case _ =>
                        complete(StatusCodes.NotFound -> ClusterHttpApiMessage("Operation not supported"))
                    }
                  case None =>
                    complete(StatusCodes.NotFound -> ClusterHttpApiMessage("Member not found"))
                }
            }
          }
      }

}

private[akka] class ClusterHttpApi(cluster: Cluster) {
  private val settings = new ClusterHttpApiSettings(cluster.system.settings.config)
  implicit val system = cluster.system
  implicit val materializer = ActorMaterializer()

  def create() = {
    val route = RouteResult.route2HandlerFlow(ClusterHttpRoute(cluster))
    Http().bindAndHandle(route, "0.0.0.0", settings.httpApiPort)
  }
}

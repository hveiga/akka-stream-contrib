package akka.cluster.http

import akka.testkit.{ImplicitSender, TestKit}

import language.postfixOps
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import org.scalatest.Matchers
import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalactic.ConversionCheckedTripleEquals
import org.scalatest.concurrent.ScalaFutures

/**
  * Created by hecortiz on 9/17/16.
  */
object ClusterSpec {
  val config =
    """
    akka.cluster {
      auto-down-unreachable-after = 0s
      periodic-tasks-initial-delay = 120 seconds // turn off scheduled tasks
      publish-stats-interval = 0 s # always, when it happens
      failure-detector.implementation-class = akka.cluster.FailureDetectorPuppet
    }
    akka.actor.provider = "akka.cluster.ClusterActorRefProvider"
    akka.remote.log-remote-lifecycle-events = off
    akka.remote.netty.tcp.port = 0
    #akka.loglevel = DEBUG
    """
}

class ClusterHttpApiSpec extends WordSpecLike with Matchers with BeforeAndAfterAll
    with ConversionCheckedTripleEquals with ScalaFutures with ScalatestRouteTest {

  "Cluster Http API" should {
    "return list of members" when {
      "calling GET /members" in {
//        val cluster = Cluster()
//        Get("/members") ~> ClusterHttpRoute(cluster) ~> check {
//          responseAs[ClusterMembers] shouldEqual ClusterMembers("something", Set(), Seq())
//          status == StatusCodes.OK
//        }
      }
    }

    "join a member" when {
      "calling POST /members with form field 'memberAddress'" in {
//        val cluster = Cluster()
//        Post("/members/?address=(memberAddress)") ~> ClusterHttpRoute(cluster) ~> check {
//          responseAs[ClusterHttpApiMessage].message shouldEqual ClusterHttpApiMessage("Operation executed")
//          status == StatusCodes.OK
//        }
      }
    }

    "return information of a member" when {
      "calling GET /members/{memberAddress}" in {
//        val cluster = Cluster()
//        Post("/members/{memberAddress}") ~> ClusterHttpRoute(cluster) ~> check {
//          responseAs[ClusterMember] shouldEqual ClusterMember("someNode", "someNodeUid", "status", Set())
//          status == StatusCodes.OK
//        }
      }
    }

    "execute leave on a member" when {
      "calling DELETE /members/{memberAddress}" in {
//        val cluster = Cluster()
//        Delete("/members/{memberAddress}") ~> ClusterHttpRoute(cluster) ~> check {
//          responseAs[ClusterHttpApiMessage].message shouldEqual ClusterHttpApiMessage("Operation executed")
//          status == StatusCodes.OK
//        }
      }
    }

    "execute down on a member" when {
      "calling PUT /members/{memberAddress} with form field operation DOWN" in {
//        val cluster = Cluster()
//        Put("/members/{memberAddress}?operation=down") ~> ClusterHttpRoute(cluster) ~> check {
//          responseAs[ClusterHttpApiMessage].message shouldEqual ClusterHttpApiMessage("Operation executed")
//          status == StatusCodes.OK
//        }
      }
    }
  }

}

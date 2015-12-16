package io.otrl.library.rest.spray

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

object CustomerRestService extends App with SimpleRoutingApp {

  implicit val actorSystem = ActorSystem("otrl-customer-rest-service")

  startServer(interface = "localhost", port = 9000) {
    pathPrefix("customer") {
      pathEndOrSingleSlash {
        post {
          complete {
            "POST / customer"
          }
        } ~
          get {
            complete {
              "GET / customer"
            }
          }
      } ~
        path(LongNumber) { customerId: Long =>
          post {
            complete {
              s"POST / customer/$customerId"
            }
          } ~
            get {
              complete {
                s"GET / customer/$customerId"
              }
            } ~
            put {
              complete {
                s"PUT / customer/$customerId"
              }
            } ~
            delete {
              complete {
                s"DELETE / customer/$customerId"
              }
            }
        }
    }
  }

}

package io.otrl.library.rest.spray

import akka.actor.ActorSystem
import io.otrl.library.rest.domain.CustomerWrapper
import io.otrl.library.rest.repository.CustomerRepository
import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.routing.SimpleRoutingApp

import scala.language.postfixOps

object CustomerRestService extends App with SimpleRoutingApp with JsonImplicits {

  implicit val actorSystem = ActorSystem("otrl-customer-rest-service")

  private implicit val customerRepository: CustomerRepository = new CustomerRepository

  startServer(interface = "localhost", port = 9000) {
    CustomerRestRouter.collectionRoute ~ CustomerRestRouter.itemRoute
  }

}

trait JsonImplicits extends SprayJsonSupport with DefaultJsonProtocol {
  //  implicit val customerFormat: RootJsonFormat[Customer] = jsonFormat1(Customer)
  implicit val customerWrapperFormat: RootJsonFormat[CustomerWrapper] = jsonFormat2(CustomerWrapper)
}

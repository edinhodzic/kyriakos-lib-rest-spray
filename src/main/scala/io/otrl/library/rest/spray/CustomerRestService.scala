package io.otrl.library.rest.spray

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.SimpleRoutingApp

import scala.language.postfixOps

// TODO this whole thing should not be necessary for the library (service yes, library no), but somehow it is necessary else there's compilation errors
object CustomerRestService extends App with SimpleRoutingApp with JsonImplicits {

  //  implicit val actorSystem = ActorSystem("otrl-customer-rest-service")

  //  private implicit val customerRepository: CustomerRepository = new CustomerRepository
  //  private implicit val customerHttpEntityConverter: CustomerHttpEntityConverter = new CustomerHttpEntityConverter

  //  startServer(interface = "localhost", port = 9000) {
  //    val customerRestRouter: ResourceRestRouter[Customer] = new ResourceRestRouter[Customer]
  //    customerRestRouter.collectionRoute ~ customerRestRouter.itemRoute
  //  }

}

trait JsonImplicits extends SprayJsonSupport with DefaultJsonProtocol {
  //  implicit val customerFormat: RootJsonFormat[Customer] = jsonFormat1(Customer)
  //  implicit val customerWrapperFormat: RootJsonFormat[CustomerConverter] = jsonFormat2(CustomerConverter)
}

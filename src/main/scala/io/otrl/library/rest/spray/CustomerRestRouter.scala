package io.otrl.library.rest.spray

import com.typesafe.scalalogging.LazyLogging
import io.otrl.library.rest.domain.{Customer, CustomerWrapper}
import io.otrl.library.rest.repository.CustomerRepository
import io.otrl.library.rest.spray.CustomerRestService._
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing.PathMatchers.Segment
import spray.routing._

import scala.util.{Failure, Success, Try}

import scala.language.postfixOps

object CustomerRestRouter extends LazyLogging {

  def collectionRoute(implicit customerRepository: CustomerRepository): Route = post {
    (pathPrefix("customer") & pathEndOrSingleSlash) {
      entity(as[CustomerWrapper]) { customerWrapper =>
        logger info s"creating $customerWrapper"
        customerRepository create new Customer(customerWrapper name) match {
          case Success(customer) =>
            respondWithHeader(Location(s"/customer/${customer.id}")) {
              complete(Created, customerWrapper)
            }
          case Failure(throwable) => complete(internalServerError(throwable))
          case _ => complete(internalServerError("unknown repository failure"))
        }
        //        complete {
        //          logger info s"creating $customerWrapper"
        //          customerRepository create new Customer(customerWrapper name) match {
        //            case Success(customer) =>
        //              Location(s"/customer/${customer.id}") // no worky!
        //              (Created, customerWrapper)
        //            case Failure(throwable) => (throwable)
        //            case _ => (InternalServerError, new scala.RuntimeException("unknown create failure"))
        //          }
        //        }
      }
    }
  }

  def itemRoute(implicit customerRepository: CustomerRepository): Route = {

    def getRoute(implicit customerId: String): Route = get {
      complete {
        logger info s"reading $customerId"
        repositoryTemplate(customerRepository read customerId) { customer =>
          new CustomerWrapper(customer.name, customerId)
        }
      }
    }

    def putRoute(implicit customerId: String): Route = put {
      entity(as[CustomerWrapper]) { customerWrapper =>
        complete {
          logger info s"updating $customerId"
          // TODO find a way to make spray-json formatters work with inheritance (Customer extends Identifiable is a problem)
          // TODO after solving the above, marshal customer
          repositoryTemplate(customerRepository update new Customer(customerWrapper name, customerWrapper.id.orNull)) { customer => NoContent }
        }
      }
    }

    def deleteRoute(implicit customerId: String): Route = delete {
      complete {
        logger info s"deleting $customerId"
        repositoryTemplate(customerRepository delete customerId) { unit => NoContent }
      }
    }

    (pathPrefix("customer") & path(Segment) & pathEndOrSingleSlash) { implicit customerId: String =>
      putRoute ~ getRoute ~ deleteRoute
    }
  }

  private def repositoryTemplate[S](repositoryFunction: (Try[Option[S]]))(successFunction: S => ToResponseMarshallable): ToResponseMarshallable =
    repositoryFunction match {
      case Success(Some(subject)) => successFunction(subject)
      case Success(None) => NotFound
      case Failure(throwable) => internalServerError(throwable)
      case _ => internalServerError("unknown repository failure")
    }

  private def internalServerError(message: String): ToResponseMarshallable =
    internalServerError(new RuntimeException(message))

  private def internalServerError(throwable: Throwable): ToResponseMarshallable =
    (InternalServerError, throwable)

}

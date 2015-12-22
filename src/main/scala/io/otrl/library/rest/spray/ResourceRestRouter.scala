package io.otrl.library.rest.spray

import com.typesafe.scalalogging.LazyLogging
import io.otrl.library.domain.Identifiable
import io.otrl.library.repository.{AbstractPartialCrudRepository, WholeUpdates}
import io.otrl.library.rest.domain.AbstractHttpEntityConverter
import io.otrl.library.rest.spray.CustomerRestService._
import spray.http.HttpEntity
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing.PathMatchers.Segment
import spray.routing._

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

// TODO make this generic
// TODO inject collaborators
class ResourceRestRouter[T <: Identifiable] extends LazyLogging {

  // TODO instead of having the complete(..) in pattern match cases, it would be good to use repositoryTemplate unless that makes the code less legible
  def collectionRoute(implicit repository: AbstractPartialCrudRepository[T], httpEntityConverter: AbstractHttpEntityConverter[T]): Route = post {
    (pathPrefix("customer") & pathEndOrSingleSlash) {
      entity(as[HttpEntity]) { httpEntity => val resource: T = httpEntityConverter.convert(httpEntity)
        logger info s"creating $resource"
        repository create resource match {
          case Success(resource) =>
            respondWithHeader(Location(s"/customer/${resource.id}")) {
              complete(Created, httpEntityConverter.convert(resource))
            }
          case Failure(throwable) => complete(internalServerError(throwable))
          case _ => complete(internalServerError("unknown repository failure"))
        }
      }
    }
  }

  def itemRoute(implicit repository: AbstractPartialCrudRepository[T] with WholeUpdates[T], httpEntityConverter: AbstractHttpEntityConverter[T]): Route = {

    def getRoute(implicit resourceId: String): Route = get {
      complete {
        logger info s"reading $resourceId"
        repositoryTemplate(repository read resourceId) { resource =>
          httpEntityConverter.convert(resource)
        }
      }
    }

    def putRoute(implicit resourceId: String): Route = put {
      entity(as[HttpEntity]) { httpEntity =>
        complete {
          logger info s"updating $resourceId"
          val resource: T = httpEntityConverter.convert(httpEntity)
          repositoryTemplate(repository update resource) { resource => NoContent }
        }
      }
    }

    def deleteRoute(implicit resourceId: String): Route = delete {
      complete {
        logger info s"deleting $resourceId"
        repositoryTemplate(repository delete resourceId) { unit => NoContent }
      }
    }

    (pathPrefix("customer") & path(Segment) & pathEndOrSingleSlash) { implicit resourceId: String =>
      getRoute ~ putRoute ~ deleteRoute
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

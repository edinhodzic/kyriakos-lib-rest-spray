package io.otrl.library.rest.spray

import com.typesafe.scalalogging.LazyLogging
import io.otrl.library.domain.Identifiable
import io.otrl.library.repository.{AbstractPartialCrudRepository, WholeUpdates}
import io.otrl.library.rest.converter.HttpEntityConverter
import spray.http.HttpEntity
import spray.http.HttpHeaders.Location
import spray.http.StatusCodes._
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing._

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

// TODO inject collaborators
/**
  * A router which exposes REST functionality for a single resource.
  * @tparam T the resource for which to expose REST functionality
  */
class SprayRestRouter[T <: Identifiable](implicit manifest: Manifest[T]) extends SimpleRoutingApp with LazyLogging {

  private val serviceUrlPath: String = manifest.runtimeClass.getSimpleName.toLowerCase

  // TODO instead of having the complete(..) in pattern match cases, it would be good to use repositoryTemplate unless that makes the code less legible
  def collectionRoute(implicit repository: AbstractPartialCrudRepository[T], httpEntityConverter: HttpEntityConverter[T]): Route = post {
    (pathPrefix(serviceUrlPath) & pathEndOrSingleSlash) {
      entity(as[HttpEntity]) { httpEntity : HttpEntity =>
        val resource: T = httpEntityConverter.toResource(httpEntity)
        logger info s"creating $resource"
        repository create resource match {
          case Success(resource) =>
            respondWithHeader(Location(s"/$serviceUrlPath/${resource.id}")) {
              complete(Created, httpEntityConverter.toHttpEntity(resource))
            }
          case Failure(throwable) => complete(internalServerError(throwable))
          case _ => complete(internalServerError("unknown repository failure"))
        }
      }
    }
  }

  def itemRoute(implicit repository: AbstractPartialCrudRepository[T] with WholeUpdates[T], httpEntityConverter: HttpEntityConverter[T]): Route = {

    def getRoute(implicit resourceId: String): Route = get {
      complete {
        logger info s"reading $resourceId"
        repositoryTemplate(repository read resourceId) { resource : T =>
          httpEntityConverter.toHttpEntity(resource)
        }
      }
    }

    def putRoute(implicit resourceId: String): Route = put {
      entity(as[HttpEntity]) { httpEntity =>
        complete {
          logger info s"updating $resourceId"
          val resource: T = httpEntityConverter.toResource(httpEntity)
          repositoryTemplate(repository update resource) { resource : T => NoContent }
        }
      }
    }

    def deleteRoute(implicit resourceId: String): Route = delete {
      complete {
        logger info s"deleting $resourceId"
        repositoryTemplate(repository delete resourceId) { unit : Unit => NoContent }
      }
    }

    (pathPrefix(serviceUrlPath) & path(Segment) & pathEndOrSingleSlash) { implicit resourceId: String =>
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

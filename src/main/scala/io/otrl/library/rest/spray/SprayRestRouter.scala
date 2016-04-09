package io.otrl.library.rest.spray

import java.util.jar.Attributes.Name.{IMPLEMENTATION_TITLE, IMPLEMENTATION_VENDOR, IMPLEMENTATION_VERSION}
import java.util.jar.{Manifest => JdkManifest}

import com.typesafe.scalalogging.LazyLogging
import io.otrl.library.crud.{Converter, CrudOperations}
import io.otrl.library.domain.Identifiable
import io.otrl.library.rest.hooks.RestHooks
import io.otrl.library.utils.ManifestUtils
import spray.http.HttpHeaders.Location
import spray.http.MediaTypes.`application/json`
import spray.http.StatusCodes._
import spray.http.{HttpEntity, HttpResponse}
import spray.httpx.marshalling.{ToResponseMarshallable => Response}
import spray.routing._

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

/**
  * An router which exposes REST functionality for a single resource.
  * @tparam T the resource for which to expose REST functionality
  */
abstract class SprayRestRouter[T <: Identifiable](implicit manifest: Manifest[T]) extends SimpleRoutingApp with RestHooks[T] with LazyLogging {

  private val serviceUrlPath: String = ManifestUtils.simpleName(manifest)

  def pingRoute: Route = get {
    (pathPrefix(serviceUrlPath) & path("ping") & pathEndOrSingleSlash) {
      complete {
        "ok"
      }
    }
  }

  def versionRoute: Route = get {
    (pathPrefix(serviceUrlPath) & path("version") & pathEndOrSingleSlash) {
      val jdkManifest: JdkManifest = new JdkManifest
      // TODO the below is not picking up the correct manifest when running via sbt
      jdkManifest read Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")

      respondWithMediaType(`application/json`) {
        complete(
          s"""{
          "implementationVendor" : "${jdkManifest getAttributes (IMPLEMENTATION_VENDOR.toString)}",
          "implementationTitle" : "${jdkManifest getAttributes (IMPLEMENTATION_TITLE.toString)}",
          "implementationVersion" : "${jdkManifest getAttributes (IMPLEMENTATION_VERSION.toString)}"
        }"""
        )
      }
    }
  }

  def collectionRoute(implicit repository: CrudOperations[T], converter: Converter[T, HttpEntity]): Route = post {
    (pathPrefix(serviceUrlPath) & pathEndOrSingleSlash) {
      entity(as[HttpEntity]) { httpEntity: HttpEntity =>
        complete {
          postHook {
            val resource: T = converter deserialise httpEntity
            logger info s"creating $resource"
            repository create resource match {
              case Success(resource) => HttpResponse(Created, converter serialise resource, List(Location(s"/$serviceUrlPath/${resource id}")))
              case Failure(throwable) => internalServerError(throwable)
              case _ => internalServerError("unknown repository failure")
            }
          }
        }
      }
    }
  }

  def itemRoute(implicit repository: CrudOperations[T], converter: Converter[T, HttpEntity]): Route = {

    def getRoute(implicit resourceId: String): Route = get {
      complete {
        getHook {
          logger info s"reading $resourceId"
          repositoryTemplate(repository read resourceId) { resource: T =>
            converter.serialise(resource)
          }
        }
      }
    }

    def putRoute(implicit resourceId: String): Route = put {
      entity(as[String]) { httpEntity =>
        complete {
          HttpResponse(NotImplemented) // TODO implement http put
        }
      }
    }

    def patchRoute(implicit resourceId: String): Route = patch {
      entity(as[String]) { httpEntity =>
        complete {
          patchHook {
            logger info s"partially updating $resourceId"
            repositoryTemplate(repository update(resourceId, httpEntity)) { resource => NoContent }
          }
        }
      }
    }

    def deleteRoute(implicit resourceId: String): Route = delete {
      complete {
        deleteHook {
          logger info s"deleting $resourceId"
          repositoryTemplate(repository delete resourceId) { unit: Unit => NoContent }
        }
      }
    }

    (pathPrefix(serviceUrlPath) & path(Segment) & pathEndOrSingleSlash) { implicit resourceId: String =>
      getRoute ~ putRoute ~ patchRoute ~ deleteRoute
    }
  }

  private def repositoryTemplate[S](repositoryFunction: (Try[Option[S]]))(successFunction: S => Response): Response =
    repositoryFunction match {
      case Success(Some(subject)) => successFunction(subject)
      case Success(None) => NotFound
      case Failure(throwable) => internalServerError(throwable)
      case _ => internalServerError("unknown repository failure")
    }

  private def internalServerError(message: String): Response =
    internalServerError(new RuntimeException(message))

  private def internalServerError(throwable: Throwable): Response =
    (InternalServerError, throwable)

}

// TODO inject collaborators
// TODO add query route
// TODO add health endpoint

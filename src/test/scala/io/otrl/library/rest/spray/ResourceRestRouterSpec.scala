package io.otrl.library.rest.spray

import io.otrl.library.repository.{AbstractPartialCrudRepository, WholeUpdates}
import io.otrl.library.rest.converter.ResourceHttpEntityConverter
import io.otrl.library.rest.domain.Resource
import io.otrl.library.rest.spray.ResourceRestRouterSpec._
import org.mockito.Matchers
import org.mockito.stubbing.OngoingStubbing
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import spray.http.ContentTypes._
import spray.http.HttpHeaders.`Content-Type`
import spray.http.StatusCodes.{Created, InternalServerError, NoContent, NotFound, OK}
import spray.http._
import spray.json._
import spray.routing.{HttpService, Route}
import spray.testkit.Specs2RouteTest

import scala.language.postfixOps
import scala.util.{Success, Try}

class ResourceRestRouterSpec extends Specification with Specs2RouteTest with HttpService with Mockito {
  isolated

  def actorRefFactory = system // connect dsl to test actor system

  private implicit val repository: AbstractPartialCrudRepository[Resource] with WholeUpdates[Resource] = mock[AbstractPartialCrudRepository[Resource] with WholeUpdates[Resource]]
  private implicit val converter: ResourceHttpEntityConverter = mock[ResourceHttpEntityConverter]

  private val resourceRestRouter: ResourceRestRouter[Resource] = new ResourceRestRouter[Resource]
  private val collectionRoute: Route = resourceRestRouter.collectionRoute
  private val itemRoute: Route = resourceRestRouter.itemRoute

  private val resourceId: String = "507c35dd8fada716c89d0013"

  private val resource: Resource = new Resource("bob") {
    id = resourceId
  }

  private val bobHttpEntity: HttpEntity = HttpEntity( """{ "name" : "bob" }""")

  "Controller post function" should {

    val bobJson: String = """{ "name" : "bob" }"""

    def mockRepositoryCreateToReturnSuccess =
      mockRepositoryCreateToReturn(Success(resource))

    def mockRepositoryCreateToReturn(triedResource: Try[Resource]) =
      repository create resource returns triedResource

    def invokePost[T](uri: String, json: String, route: Route): RouteResult =
      Post(uri, jsonHttpEntity(json)).withHeaders(contentTypeJson) ~> route

    def verifyPost[T](uri: String, json: String, route: Route)(block: => T) =
      invokePost(uri, json, route) ~> check(block)

    "invoke repository create function" in {
      mockConverter(resource, bobHttpEntity)
      mockRepositoryCreateToReturnSuccess
      invokePost("/resource", bobJson, collectionRoute)
      there was one(repository).create(resource)
    }

    "return http created when repository create succeeds" in {
      mockConverter(resource, bobHttpEntity)
      mockRepositoryCreateToReturnSuccess
      verifyPost("/resource", bobJson, collectionRoute) {
        status === Created
      }
    }

    "return correct location header when repository create succeeds" in {
      mockConverter(resource, bobHttpEntity)
      mockRepositoryCreateToReturnSuccess
      verifyPost("/resource", bobJson, collectionRoute) {
        val locationHeader: Option[HttpHeader] = header("Location")
        locationHeader.isDefined === true
        locationHeader.get.value === s"/resource/$resourceId"
      }
    }

    "return resource as response body when repository create succeeds" in {
      mockConverter(resource, bobHttpEntity)
      mockRepositoryCreateToReturnSuccess
      verifyPost("/resource", bobJson, collectionRoute) {
        JsonParser(response.entity.asString) should beEqualTo(JsonParser(bobJson))
      }
    }

    "return http internal server error when repository create fails" in {
      repository create Matchers.any(classOf[Resource]) throws new RuntimeException
      verifyPost("/resource", bobJson, collectionRoute) {
        status === InternalServerError
      }
    }
  }

  "Resource rest router get function" should {

    def mockRepositoryReadToReturn(triedMaybeResource: Try[Option[Resource]]) =
      repository read anyString returns triedMaybeResource

    "invoke repository read function" in {
      mockConverterWith(bobHttpEntity)
      mockRepositoryReadToReturn(Success(Some(resource)))
      Get("/resource/123") ~> itemRoute
      there was one(repository).read("123")
    }

    "return http ok status when repository read succeeds with some" in {
      mockConverterWith(bobHttpEntity)
      mockRepositoryReadToReturn(Success(Some(resource)))
      Get("/resource/123") ~> itemRoute ~> check {
        status === OK
      }
    }

    "return http not found status when repository read succeeds with none" in {
      mockConverterWith(bobHttpEntity)
      mockRepositoryReadToReturn(Success(None))
      Get("/resource/123") ~> itemRoute ~> check {
        status === NotFound
      }
    }

    "return http internal server error status when repository read fails with exception" in {
      repository read anyString throws new RuntimeException
      Get("/resource/123") ~> itemRoute ~> check {
        status === InternalServerError
      }
    }
  }

  //  "Resource rest router put function" should {
  //    "do something" in {
  // TODO implement me
  //    }
  //  }

  "Resource rest router delete function" should {

    def mockRepositoryDeleteToReturn(triedMaybeUnit: Try[Option[Unit]]) =
      repository delete anyString returns triedMaybeUnit

    "invoke repository delete function" in {
      mockRepositoryDeleteToReturn(Success(Some()))
      Delete("/resource/123") ~> itemRoute
      there was one(repository).delete("123")
    }

    "return http no content status when repository delete succeeds with some" in {
      mockRepositoryDeleteToReturn(Success(Some()))
      Delete("/resource/123") ~> itemRoute ~> check {
        status === NoContent
      }
    }

    "return http not found status when repository delete succeeds with none" in {
      mockRepositoryDeleteToReturn(Success(None))
      Delete("/resource/123") ~> itemRoute ~> check {
        status === NotFound
      }
    }

    "return http internal server error status when repository delete fails with exception" in {
      repository read anyString throws new RuntimeException
      Delete("/resource/123") ~> itemRoute ~> check {
        status === InternalServerError
      }
    }
  }

  //  "Resource rest router query function" should {
  //    "do something" in {
  // TODO implement me
  //    }
  //  }

  def mockConverter(resource: Resource, httpEntity: HttpEntity) = {
    mockConverterWith(resource)
    mockConverterWith(httpEntity)
  }

  def mockConverterWith(resource: Resource): OngoingStubbing[Resource] = {
    converter.toResource(Matchers.any(classOf[HttpEntity])) returns resource
  }

  def mockConverterWith(httpEntity: HttpEntity): OngoingStubbing[HttpEntity] = {
    converter.toHttpEntity(Matchers.any(classOf[Resource])) returns httpEntity
  }

}

object ResourceRestRouterSpec {

  private def jsonHttpEntity[T](json: String): HttpEntity = HttpEntity(`application/json`, json)

  private val contentTypeJson: `Content-Type` = HttpHeaders.`Content-Type`(`application/json`)

}

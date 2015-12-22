package io.otrl.library.rest.converter

import io.otrl.library.rest.domain.{AbstractHttpEntityConverter, Resource}
import spray.http.HttpEntity

class ResourceHttpEntityConverter extends AbstractHttpEntityConverter[Resource] {

  override def toResource(httpEntity: HttpEntity): Resource = new Resource("value")

  override def toHttpEntity(resource: Resource): HttpEntity = HttpEntity( """{ "data" : "value" } """)

}

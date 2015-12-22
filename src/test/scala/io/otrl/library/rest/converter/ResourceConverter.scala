package io.otrl.library.rest.converter

import io.otrl.library.rest.domain.Resource
import spray.http.HttpEntity

class ResourceConverter extends HttpEntityConverter[Resource] {

  override def toResource(httpEntity: HttpEntity): Resource = new Resource("value")

  override def toHttpEntity(resource: Resource): HttpEntity = HttpEntity( """{ "data" : "value" } """)

}
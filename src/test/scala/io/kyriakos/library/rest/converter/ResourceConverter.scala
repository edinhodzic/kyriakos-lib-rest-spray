package io.kyriakos.library.rest.converter

import io.kyriakos.library.crud.Converter
import io.kyriakos.library.rest.domain.Resource
import spray.http.HttpEntity

class ResourceConverter extends Converter[Resource, HttpEntity] {

  override def serialise(subject: Resource): HttpEntity = HttpEntity( """{ "data" : "value" } """)

  override def deserialise(subject: HttpEntity): Resource = Resource("value")

}

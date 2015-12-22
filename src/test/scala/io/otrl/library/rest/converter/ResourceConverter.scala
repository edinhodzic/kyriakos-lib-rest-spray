package io.otrl.library.rest.converter

import io.otrl.library.repository.Converter
import io.otrl.library.rest.domain.Resource
import spray.http.HttpEntity

class ResourceConverter extends Converter[Resource, HttpEntity] {

  override def serialise(subject: Resource): HttpEntity = HttpEntity( """{ "data" : "value" } """)

  override def deserialise(subject: HttpEntity): Resource = new Resource("value")

}

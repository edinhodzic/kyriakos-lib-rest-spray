package io.otrl.library.rest.domain

import io.otrl.library.domain.Identifiable
import spray.http.HttpEntity

abstract class AbstractHttpEntityConverter[T <: Identifiable] {

  def toResource(httpEntity: HttpEntity): T

  def toHttpEntity(resource: T): HttpEntity

}

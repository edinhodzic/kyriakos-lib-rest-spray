package io.otrl.library.rest.converter

import io.otrl.library.domain.Identifiable
import spray.http.HttpEntity

/**
  * Converts an object into a [[HttpEntity]] and vice versa.
  * @tparam T the type of the object to convert from and to
  */
abstract class HttpEntityConverter[T <: Identifiable] {

  def toResource(httpEntity: HttpEntity): T

  def toHttpEntity(resource: T): HttpEntity

}

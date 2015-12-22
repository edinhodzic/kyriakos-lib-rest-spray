package io.otrl.library.rest.domain

import io.otrl.library.domain.Identifiable
import spray.http.HttpEntity

abstract class AbstractHttpEntityConverter[T <: Identifiable] {

  def convert(httpEntity: HttpEntity): T

  def convert(resource: T): HttpEntity

}

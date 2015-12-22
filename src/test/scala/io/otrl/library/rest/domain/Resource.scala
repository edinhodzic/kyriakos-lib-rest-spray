package io.otrl.library.rest.domain

import io.otrl.library.domain.Identifiable

case class Resource(data: String) extends Identifiable {
  def this(data: String, id: String) = this(data)
}

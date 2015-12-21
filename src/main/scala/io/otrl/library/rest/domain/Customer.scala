package io.otrl.library.rest.domain

import io.otrl.library.domain.Identifiable

case class Customer(name: String) extends Identifiable {
  def this(name: String, id: String) = this(name)
}

// TODO needed as spray-json won't (de)serialize with a trait in the class hierarchy, find a neater soluition
case class CustomerWrapper(name: String, id: Option[String]) {
  def this(name: String, id: String) = this(name, Option(id))
}

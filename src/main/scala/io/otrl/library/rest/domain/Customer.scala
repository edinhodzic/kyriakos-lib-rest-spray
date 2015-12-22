package io.otrl.library.rest.domain

import io.otrl.library.domain.Identifiable

// TODO move to otrl-service-rest-customer
case class Customer(name: String) extends Identifiable {
  def this(name: String, id: String) = this(name)
}

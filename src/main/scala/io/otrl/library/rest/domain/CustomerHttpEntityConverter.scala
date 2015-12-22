package io.otrl.library.rest.domain

import spray.http.HttpEntity

// TODO move to otrl-service-rest-customer
class CustomerHttpEntityConverter extends AbstractHttpEntityConverter[Customer] {

  override def convert(httpEntity: HttpEntity): Customer =
    new Customer("firstname")

  override def convert(resource: Customer): HttpEntity =
    HttpEntity.apply( """{ "name" : "hardcoded" } """)

}

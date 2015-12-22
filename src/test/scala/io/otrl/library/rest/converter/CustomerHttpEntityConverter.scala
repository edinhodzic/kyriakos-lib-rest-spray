package io.otrl.library.rest.converter

import io.otrl.library.rest.domain.{AbstractHttpEntityConverter, Customer}
import spray.http.HttpEntity

// TODO move to otrl-service-rest-customer
class CustomerHttpEntityConverter extends AbstractHttpEntityConverter[Customer] {

  override def toResource(httpEntity: HttpEntity): Customer =
    new Customer("firstname")

  override def toHttpEntity(resource: Customer): HttpEntity =
    HttpEntity.apply( """{ "name" : "hardcoded" } """)

}

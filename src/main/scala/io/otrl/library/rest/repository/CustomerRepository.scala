package io.otrl.library.rest.repository

import io.otrl.library.repository.h2.AbstractH2CrudRepository
import io.otrl.library.rest.domain.Customer

class CustomerRepository extends AbstractH2CrudRepository[Customer]

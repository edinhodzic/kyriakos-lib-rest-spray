# About

This is a small REST service abstraction based on [Spray](http://spray.io/). It assumes and operates on a set of conventions around CRUD and query operations.

# Abstractions

This project makes and uses the abstractions depicted in the below diagram.

![Spray abstractions](https://cloud.githubusercontent.com/assets/4981314/12087767/9921a146-b2cd-11e5-8831-d9afde9dd614.jpg)

## Service conventions

This service abstraction conforms to a set of conventions regarding REST.

### REST

| Method | Description    | Collection URI HTTP response       | Item URI HTTP response             |
|--------|----------------|------------------------------------|------------------------------------|
| POST   | Create         | `201 Created` / `409 Conflict`?    | unsupported                        |
| GET    | Read           | unsupported                        | `200 Ok` / `404 Not Found`         |
| PUT    | Whole update   | unsupported                        | `204 No Content` / `404 Not Found` |
| PATCH  | Partial update | unsupported                        | `204 No Content` / `404 Not Found` |
| DELETE | Delete         | unsupported                        | `204 No Content` / `404 Not Found` |

    TODO add query endpoint convention
    TODO add ping endpoint convention
    TODO add health endpoint convention

# What's under the hood?

Implementation:

- [Scala](http://www.scala-lang.org/)
- [Spray](http://spray.io/)
- [`otrl-lib-repository-h2`](https://github.com/otrl/otrl-lib-repository-h2) (should swap for the `-mariadb` or `-mongo` alternative implementations)

Testing:

- [Specs2](https://etorreborre.github.io/specs2/)
- [Spray Testkit](http://spray.io/documentation/1.2.2/spray-testkit/)

# Quick start

The [`otrl-rest-micro-service-spray.g8`](https://github.com/otrl/otrl-rest-micro-service-spray.g8) project can be used to very quickly create RESTful web service projects from scratch which use this library and therefore the above conventions. Alternatively, for a custom implementation, follow the below steps to implement a simple service using the abstraction API and then run and invoke service operations.

## Implementation

An implementation needs to have four things:

- router : this will handle HTTP requests
- repository : this will perform database queries
- converter : this will convert a Spray `HttpEntity` into a domain model object and vice versa
- service : this will bootstrap the Akka actor system and Spray service 

The router and repository rely on abstractions within another library hence they are quite succinct pieces of code. Suppose we were implementing a person REST service; we would need a:

- `PersonRestRouter`
- `PersonCrudRepository`
- `PersonConverter`
- `PersonRestService`

Let's build this bottom up.

### Domain model implementation
```scala
case class Person(data: String) extends Identifiable
```

### Converter implementation
```scala
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._

implicit object HttpEntityConverter extends Converter[Person, HttpEntity] with LazyLogging {

  override def serialise(person: Person): HttpEntity =
    marshal(person) match {
      case Right(httpEntity) =>
        logger debug s"serialised $person into $httpEntity"
        httpEntity
      case Left(throwable) => throw throwable
    }

  override def deserialise(httpEntity: HttpEntity): Person =
    httpEntity.as[Person] match {
      case Right(person) =>
        logger debug s"deserialised $httpEntity into $person"
        person
      case Left(deserializationError) => throw new RuntimeException(
        s"error deserialising $httpEntity into person (info:$deserializationError)")
    }
}
```

### Repository implementation
```scala
class PersonCrudRepository extends AbstractH2CrudRepository[Person]
```
The above uses a H2 repository implementation and should be seamlessly interchangeable with other implementations.
    
### Router implementation
```scala
object PersonRestRouter extends DefaultSprayRestRouter[Person] // or KamonSprayRestRouter[Person]
```
### Service implementation
```scala
object PersonRestService extends App with SimpleRoutingApp {

  implicit val actorSystem = ActorSystem("person-rest-service")

  private implicit val personCrudRepository: PersonCrudRepository = new PersonCrudRepository

  private implicit val personConverter: PersonConverter = new PersonConverter

  startServer(interface = "localhost", port = 9000) {
    PersonRestRouter.collectionRoute ~ PersonRestRouter.itemRoute
  }

}
```
## Usage

With the above implementation in place, the service shold be ready to perform CRUD and query operations over `Person` domain objects.

### Create a resource
```
$ curl -iL -X POST http://localhost:9000/person -H content-type:application/json -d '{ "data" : "bob" }'
HTTP/1.1 201 Created
Server: spray-can/1.3.3
Date: Tue, 22 Dec 2015 17:46:11 GMT
Location: /person/123
Content-Type: text/plain; charset=UTF-8
Content-Length: 21

{ "data" : "bob" }
```
    
### Read a resource    
```
$ curl -iL http://localhost:9000/person/123
HTTP/1.1 200 OK
Server: spray-can/1.3.3
Date: Tue, 22 Dec 2015 17:46:29 GMT
Content-Type: text/plain; charset=UTF-8
Content-Length: 21

{ "data" : "bob" }
```

### Update a resource
    
    TODO document this

### Delete a resource 
```
$ curl -iL -X DELETE http://localhost:9000/person/123
HTTP/1.1 204 No Content
Server: spray-can/1.3.3
Date: Tue, 22 Dec 2015 17:46:37 GMT
Content-Length: 0
```

### Query a resource
    
    TODO document this

## Incomplete features

- [ ] update and query operations

## Future development ideas

- hypermedia
- watch this space

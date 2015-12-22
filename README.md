# About

This is a small REST service abstraction based on [Spray](http://spray.io/). It assumes and operates on a set of conventions around CRUD and query operations.

# Abstractions

This project makes and uses the abstractions depicted in the below diagram.

    TODO add class diagram

![REST service abstractions]()

## Service conventions

This service abstraction conforms to a set of conventions regarding REST.

### REST

| Method | Description | Collection URI HTTP response       | Item URI HTTP response             |
|--------|-------------|------------------------------------|------------------------------------|
| POST   | Create      | `201 Created` / `409 Conflict`?    | unsupported                        |
| GET    | Read        | unsupported                        | `200 Ok` / `404 Not Found`         |
| PUT    | Update      | unsupported                        | `204 No Content` / `404 Not Found` |
| DELETE | Delete      | unsupported                        | `204 No Content` / `404 Not Found` |

    TODO add query endpoint convention

# What's under the hood?

Implementation:

- [Scala](http://www.scala-lang.org/)
- [Spray](http://spray.io/)

Testing:

- [Specs2](https://etorreborre.github.io/specs2/)
- [Spray Testkit](http://spray.io/documentation/1.2.2/spray-testkit/)

# Quick start

Follow the below steps to implement a simple service using the abstraction API and then run and invoke service operations.

## Implementation

An implementation needs to have three things:

- router : this will handle HTTP requests
- repository : this will perform database queries
- converter : this will convert a Spray `HttpEntity` into a domain model object and vice versa
- service : this will bootstrap the Akka actor system and Spray service 

The router and repository rely on abstractions within another library hence they are quite succinct pieces of code. Suppose we were implementing a user REST service; we would need a:

- `UserRestRouter`
- `UserCrudRepository`
- `UserConverter`
- `UserRestService`

Let's build this bottom up.

### Domain model implementation
```scala
case class User(data: String) extends Identifiable
```

### Converter implementation
```scala
class UserConverter extends Converter[User, HttpEntity] {

  override def serialise(user: User): HttpEntity = HttpEntity( """{ "data" : "value" } """)

  override def deserialise(httpEntity: HttpEntity): User = new User("value")

}
```
    TODO the above is a stub, correct it to an actual implementation

### Repository implementation
```scala
class UserCrudRepository extends AbstractH2CrudRepository[User]
```
The above uses a H2 repository implementation (`TODO make this implementation available`) and should be seamlessly interchangeable with other implementations.
    
### Router implementation
```scala
object UserRestRouter extends SprayRestRouter[User]
```
### Service implementation
```scala
object UserRestService extends App with SimpleRoutingApp {

  implicit val actorSystem = ActorSystem("user-rest-service")

  private implicit val userCrudRepository: UserCrudRepository = new UserCrudRepository

  private implicit val userConverter: UserConverter = new UserConverter

  startServer(interface = "localhost", port = 9000) {
    UserRestRouter.collectionRoute ~ UserRestRouter.itemRoute
  }

}
```
## Usage

    TODO document this

### Create a resource
```
$ curl -iL -X POST http://localhost:9000/user -H content-type:application/json -d '{ "data" : "bob" }'
HTTP/1.1 201 Created
Server: spray-can/1.3.3
Date: Tue, 22 Dec 2015 17:46:11 GMT
Location: /user/123
Content-Type: text/plain; charset=UTF-8
Content-Length: 21

{ "data" : "bob" }
```
    
### Read a resource    
```
$ curl -iL http://localhost:9000/user/123
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
$ curl -iL -X DELETE http://localhost:9000/user/123
HTTP/1.1 204 No Content
Server: spray-can/1.3.3
Date: Tue, 22 Dec 2015 17:46:37 GMT
Content-Length: 0
```

### Query a resource
    
    TODO document this

# What's next?

- [ ] write a REST service using this library
- [ ] derive from the above service, a giter8 tempalte
- [ ] use the giter8 template to build micro services from scratch

    TODO provide a standard set of repository implementations for use with the above

## Incomplete features

- [ ] update and query operations

## Future development ideas

- [ ] watch this space

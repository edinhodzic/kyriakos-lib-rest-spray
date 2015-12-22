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

Let's build this bottom up.

### Domain model implementation
```scala
case class User(data: String) extends Identifiable
```

### Converter implementation
```scala
class UserConverter extends HttpEntityConverter[User] {

  override def toResource(httpEntity: HttpEntity): User = new User("bob")

  override def toHttpEntity(user: User): HttpEntity = HttpEntity( """{ "data" : "bob" } """)

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
    
    TODO document this
    
### Read a resource
    
    TODO document this

### Update a resource
    
    TODO document this

### Delete a resource
    
    TODO document this

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

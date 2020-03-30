package repositories

import java.time.LocalDateTime

import models.Client
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test._

@RunWith(classOf[JUnitRunner])
class ClientRepositorySpec extends Specification {

  "ClientRepository" should {

    var clientId: Long = -1L
    var clientCount: Long = -1L

    "save a client" in new WithApplication {
      val repository = app.injector.instanceOf[ClientRepository]

      val created  = repository.create(Client(-1, "Foobar", LocalDateTime.now))
      clientId = created.id
      created.isNew must beFalse
    }

    "not retrieve a client that doesn't exist" in new WithApplication {
      val repository = app.injector.instanceOf[ClientRepository]
      repository.read(-42) must beNone
    }

    "retrieve a client by id" in new WithApplication {
      val repository = app.injector.instanceOf[ClientRepository]
      val retrieved = repository.read(clientId)
      retrieved.map(_.id) must beSome(clientId)
      retrieved.map(_.name) must beSome("Foobar")
    }

    "count the number of clients before deletion" in new WithApplication {
      val repository = app.injector.instanceOf[ClientRepository]
      clientCount = repository.count
      clientCount must beGreaterThan(0L)
    }

    "delete a client by id" in new WithApplication {
      val repository = app.injector.instanceOf[ClientRepository]
      repository.delete(clientId) must beTrue
    }

    "count the number of clients after deletion" in new WithApplication {
      val repository = app.injector.instanceOf[ClientRepository]
      repository.count must beEqualTo(clientCount - 1)
    }
  }
}

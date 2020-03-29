package repositories

import java.time.LocalDateTime

import models.Client
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting

class ClientRepositoryTest extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "ClientRepository" should {

    var client: Client = null
    var clientId: Long = -1L
    var clientCount: Long = -1L

    "save a client" in {
      val repository = inject[ClientRepository]

      val created  = repository.create(Client(-1, "Foobar", LocalDateTime.now))
      client = created
      clientId = created.id
      created.isNew mustBe false
    }

    "not retrieve a client that doesn't exist" in {
      val repository = inject[ClientRepository]
      repository.read(-42) mustBe None
    }

    "retrieve a client by id" in {
      val repository = inject[ClientRepository]
      val retrieved = repository.read(clientId)
      retrieved.map(_.id) mustBe Some(client.id)
      retrieved.map(_.name) mustBe Some(client.name)
    }

    "count the number of emails before deletion" in {
      val repository = inject[ClientRepository]
      clientCount = repository.count
      clientCount mustBe 1
    }

    "delete a client by id" in {
      val repository = inject[ClientRepository]
      repository.delete(clientId) mustBe true
    }

    "count the number of emails after deletion" in {
      val repository = inject[ClientRepository]
      clientCount = repository.count
      clientCount mustBe 0
    }
  }
}

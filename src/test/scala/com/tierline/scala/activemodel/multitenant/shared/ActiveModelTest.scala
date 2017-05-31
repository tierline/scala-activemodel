package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.MultitenancyException
import com.tierline.scala.activemodel.{ActiveModelException, ActiveModelSchema, NativeQuery, TestSuite}
import com.tierline.scala.activemodel.multitenant.domain._
import com.tierline.scala.activemodel.singletenant.ActiveModelSessionFactory
import org.squeryl.PrimitiveTypeMode._

class ActiveModelTest extends TestSuite {

  override val schema: ActiveModelSchema = SharedSchema
  //      schema.setLogger("##")

  val sharedSchema = SharedSchema

  var mainTenant: Tenant = _

  var tenantName: String = "tierline"
  var tenantId: String = "C-101"

  def addTenantColumns() = {
    NativeQuery(schema).update("ALTER TABLE Channel ADD tenantId VARCHAR(8) not null;")
  }

  override def beforeAll {
    super.beforeAll
    addTenantColumns()
    transaction {
      this.mainTenant = new Tenant(tenantId, tenantName).create()
    }
  }

  test("save tenant is not multitenancy model") {
    val newTenantId = "aaa"
    val newTenantName = "other"
    new Tenant(newTenantId, newTenantName).save()
    Tenant.findByName(newTenantName) match {
      case Some(a) => succeed
      case None => fail()
    }
  }

  test("save multitenancy model") {
    val tenant = Tenant.findByName(tenantName).getOrElse(fail())

    Multitenancy.currentTenant.withValue(tenant.tenantId) {

      val channel = new Channel()
      if (!channel.save()) fail("fail save")
      val result = selectHead("Channel", channel.id, Seq("tenantId"))
      val resultTenantId = result("tenantId")
      assert(resultTenantId == this.mainTenant.tenantId, s"$resultTenantId == ${this.mainTenant.tenantId}")

    }
  }

  test("fail save multitenancy model without setting current tenant") {
    val channel = new Channel()
    assertThrows[MultitenancyException] {
      channel.save()
    }
  }


  test("create multitenancy model") {
    val tenant = Tenant.findByName(tenantName).getOrElse(fail())

    Multitenancy.currentTenant.withValue(tenant.tenantId) {

      val channel = new Channel().create()

      val result = selectHead("Channel", channel.id, Seq("tenantId"))
      val resultTenantId = result("tenantId")
      assert(resultTenantId == this.mainTenant.tenantId, s"$resultTenantId == ${this.mainTenant.tenantId}")

    }
  }

  test("fail create multitenancy model without setting current tenant") {
    val channel = new Channel()

    assertThrows[MultitenancyException] {
      channel.create()
    }
  }


  test("update multitenancy model") {
    val tenant = Tenant.findByName(tenantName).getOrElse(fail())

    val firstName = "foo"
    val secondName = "buzz"
    Multitenancy.currentTenant.withValue(tenant.tenantId) {

      val channel = new Channel(firstName).create()
      channel.name = secondName
      channel.update()

      val result = selectHead("Channel", channel.id, Seq("name", "tenantId"))
      val name = result("name")
      assert(name == secondName, s"$name == ${channel.name}")
    }
  }

  test("delete multitenancy model") {
    val tenant = Tenant.findByName(tenantName).getOrElse(fail())

    Multitenancy.currentTenant.withValue(tenant.tenantId) {

      val channel = new Channel().create()

      channel.delete()

      Channel.findById(channel.id) match {
        case Some(c) => fail()
        case None => succeed
      }

    }
  }


  test("fail delete if other tenant model") {
    val newTenantId = "C-999"
    val newTenantName = "foo"
    val fooChannelName = "foo"
    val mainChannelName = "main"

    val fooTenant = new Tenant(newTenantId, newTenantName).create()

    var fooChannelId = 0L

    var mainChannelId = 0L

    Multitenancy.currentTenant.withValue(fooTenant.tenantId) {
      fooChannelId = new Channel(fooChannelName).create().id
    }

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      mainChannelId = new Channel(mainChannelName).create().id
    }

    Multitenancy.currentTenant.withValue(fooTenant.tenantId) {

      val c = Channel.findById(fooChannelId).getOrElse(fail())
      assert(c.delete())

      val mainC = Channel.findById(mainChannelId).getOrElse(fail())
      assert(!mainC.delete())
    }

    Multitenancy.currentTenant.withValue(fooTenant.tenantId) {
      Channel.findById(fooChannelId) match {
        case Some(c) => fail()
        case None => succeed
      }
    }

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      Channel.findById(mainChannelId) match {
        case Some(c) => succeed
        case None => fail()
      }
    }
  }
  test("find model") {

  }
}

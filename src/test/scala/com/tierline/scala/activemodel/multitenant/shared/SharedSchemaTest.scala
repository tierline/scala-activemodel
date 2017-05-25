package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.{ActiveModelSchema, NativeQuery, TestSuite}
import com.tierline.scala.activemodel.multitenant.domain._
import org.squeryl.PrimitiveTypeMode._

class SharedSchemaTest extends TestSuite {

  override val schema: ActiveModelSchema = SharedSchema

  val sharedSchema = SharedSchema

  var tenant: Tenant = _

  var tenantName: String = "tierline"
  var tenantId: String = "C-101"

  def addTenantColumns() = {
    NativeQuery(schema).update("ALTER TABLE Channel ADD tenantId VARCHAR(8) not null;")
  }

  override def beforeAll {
    super.beforeAll
    addTenantColumns()
    transaction {
      this.tenant = new Tenant(tenantId, tenantName).create()
    }
  }


  test("save tenant without dynamic variable tenantId") {
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
      val result = selectTable("Channel", channel.id, Seq("tenantId"))
      val resultTenantId = result("tenantId")
      assert(resultTenantId == this.tenant.tenantId, s"$resultTenantId == ${this.tenant.tenantId}")

    }
  }

  
}

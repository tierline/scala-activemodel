package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.MultitenancyException
import com.tierline.scala.activemodel.multitenant.domain.{Channel, Tenant}

class InsertActiveModelTest extends ActiveModelTest {

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

}

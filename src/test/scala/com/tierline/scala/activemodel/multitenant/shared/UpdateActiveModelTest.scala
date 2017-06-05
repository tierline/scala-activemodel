package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.domain.{Channel, Tenant}

class UpdateActiveModelTest extends ActiveModelTest {

  test("update multitenancy model") {

    val tenant = mainTenant

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
}

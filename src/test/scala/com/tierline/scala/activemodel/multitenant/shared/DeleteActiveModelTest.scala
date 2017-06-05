package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.domain.{Channel, Tenant}

class DeleteActiveModelTest extends ActiveModelTest {

  test("delete multitenancy model") {
    val tenant = mainTenant

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
}

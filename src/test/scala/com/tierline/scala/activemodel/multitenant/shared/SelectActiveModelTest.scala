package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.domain.{Channel, Tenant}

class SelectActiveModelTest extends ActiveModelTest {

  test("find model") {
    val secondTenantId = "aaa"
    val secondTenantName = "other"
    val secondTenant = new Tenant(secondTenantId, secondTenantName).create()

    var mainChannel: Option[Channel] = None
    var secondChannel: Option[Channel] = None
    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      mainChannel = Some(new Channel("main").create())
    }

    Multitenancy.currentTenant.withValue(secondTenant.tenantId) {
      secondChannel = Some(new Channel("second").create())
    }

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      Channel.findById(secondChannel.get.id) match {
        case Some(c) => fail(s"id = ${c.id}  name = ${c.name}")
        case None => succeed
      }
    }

  }

  //  test("find join model") {
  //    val secondTenantId = "aaa"
  //    val secondTenantName = "other"
  //    val secondTenant = new Tenant(secondTenantId, secondTenantName).create()
  //
  //    var mainChannel: Option[Channel] = None
  //    var secondChannel: Option[Channel] = None
  //    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
  //      mainChannel = Some(new Channel("main").create())
  //    }
  //
  //    Multitenancy.currentTenant.withValue(secondTenant.tenantId) {
  //      secondChannel = Some(new Channel("second").create())
  //    }
  //
  //    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
  //      Channel.findById(secondChannel.get.id) match {
  //        case Some(c) => fail(s"id = ${c.id}  name = ${c.name}")
  //        case None => succeed
  //      }
  //    }

}

}

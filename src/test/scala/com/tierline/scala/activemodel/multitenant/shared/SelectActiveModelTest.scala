package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.domain.{Channel, Comment, Tenant}
import org.squeryl.PrimitiveTypeMode._


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

  test("find join model") {

    val name = "foo"
    val commentValue = "hello"

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      val mainChannel = new Channel(name).create()

      val c1 = new Comment(commentValue)
      mainChannel.addComment(c1)

      assert(1 == mainChannel.comments.toSeq.size)
    }

    Multitenancy.currentTenant.withValue(tenants(1).tenantId) {
      val channels = Channel.find(c => c.name === name)
      assert(channels.isEmpty)

      val commnets = Comment.find(c => c.value === commentValue)
      assert(commnets.isEmpty)
    }

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      val channels = Channel.find(c => c.name === name)
      assert(channels.nonEmpty)

      val commnets = Comment.find(c => c.value === commentValue)
      assert(commnets.nonEmpty)
    }

  }

}

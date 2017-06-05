package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.collection.AnyWhere
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

  test("find child model") {

    val name = "foo"
    val commentValue = "hello"
    val numberOfComment = 12

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      val mainChannel = new Channel(name).create()


      1 to numberOfComment foreach { n =>
        mainChannel.addComment(new Comment(commentValue + n))
      }

      assert(numberOfComment == mainChannel.comments.toSeq.size)
    }

    Multitenancy.currentTenant.withValue(tenants(1).tenantId) {
      val channels = Channel.find(c => c.name === name)
      assert(channels.isEmpty)

      val comments = Comment.find(c => c.value like AnyWhere(commentValue))
      assert(comments.isEmpty)
    }

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      val channels = Channel.find(c => c.name === name)
      assert(channels.nonEmpty)

      val comments = Comment.find(c => c.value like AnyWhere(commentValue))
      assert(comments.nonEmpty)
    }

  }

  test("find model by join") {

    val name = "foo"
    val commentValue = "hello"
    val numberOfComment = 12
    var mainChannel: Channel = new Channel("__dummy__")

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      mainChannel = new Channel(name).create()


      1 to numberOfComment foreach { n =>
        mainChannel.addComment(new Comment(commentValue + n))
      }

      assert(numberOfComment == mainChannel.comments.toSeq.size)
    }

    Multitenancy.currentTenant.withValue(tenants(1).tenantId) {
      val channels = Channel.find(c => c.name === name)
      assert(channels.isEmpty)

      val comments = Comment.findWithChannel(mainChannel, commentValue)

      assert(comments.isEmpty)
    }

    Multitenancy.currentTenant.withValue(mainTenant.tenantId) {
      val channels = Channel.find(c => c.name === name)
      assert(channels.nonEmpty)

      debugSql()
      val comments = Comment.findWithChannel(mainChannel, commentValue)
      assert(comments.nonEmpty)
    }

  }
}

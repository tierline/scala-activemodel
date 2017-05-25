package com.tierline.scala.activemodel.multitenant.domain

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant.shared.SharedSchema
import org.squeryl.dsl.ManyToOne

object Comment extends Repository[Comment]

case class Comment(
  var id: Long,
  var name: String,
  var price: Long) extends ActiveModel with MultitenancyModel {

  var channelId: Long = _

  def this() = this(0L, "", 0L)

  def this(cartId: Long) {
    this()
    this.channelId = cartId
  }

  lazy val channel: ManyToOne[Channel] = SharedSchema.channelToComment.right(this)
}


package com.tierline.scala.activemodel.multitenant.domain

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant.shared.SharedSchema
import org.squeryl.dsl.ManyToOne

object Comment extends Repository[Comment]

case class Comment(
  var id: Long,
  var value: String) extends ActiveModel {

  var channelId: Long = _

  def this() {
    this(0L, "")
  }

  def this(value: String) {
    this(0L, value)
  }

  lazy val channel: ManyToOne[Channel] = SharedSchema.channelToComment.right(this)
}


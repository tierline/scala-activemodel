package com.tierline.scala.activemodel.multitenant.domain

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.collection.AnyWhere
import com.tierline.scala.activemodel.multitenant.shared.SharedSchema
import org.squeryl.dsl.ManyToOne
import org.squeryl.PrimitiveTypeMode._

object Comment extends Repository[Comment] {

  def findWithChannel(channel: Channel, value: String): Seq[Comment] = {
    from(SharedSchema.channel, SharedSchema.comment)((ch, co) =>
      where((ch.id === co.channelId) and (co.value like AnyWhere(value)))
        select co
        orderBy (co.id asc)
    ).toSeq
  }

}

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


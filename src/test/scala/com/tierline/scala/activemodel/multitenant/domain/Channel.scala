package com.tierline.scala.activemodel.multitenant.domain

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant.shared.SharedSchema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient
import org.squeryl.dsl.OneToMany

import scala.collection.mutable.Map
import scala.language.dynamics

object Channel extends Repository[Channel] {

  def countByName(name: String): Long = {
    val r = from(repo)(e =>
      where(e.name === name)
        compute (count(e.id)))
    debug(r.statement)
    r
  }
}

case class Channel(
  var id: Long,
  var name: String,
  var size: Long) extends ActiveModel {


  def this() = this(0L, "", 0L)

  def this(name: String, size: Long) = this(0L, name, size)

  lazy val comment: OneToMany[Comment] = SharedSchema.channelToComment.left(this)

}

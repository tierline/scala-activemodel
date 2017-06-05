package com.tierline.scala.activemodel.multitenant.domain

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant.shared.SharedSchema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Transient
import org.squeryl.dsl.ast.EqualityExpression
import org.squeryl.dsl.{NumericalExpression, OneToMany}

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

  override def findById(id: Long): Option[Channel] = {
    val query = repo.where {
      r => (r.id === id)
    }
    query.toSeq.headOption
  }
}

case class Channel(
  var id: Long,
  var name: String) extends ActiveModel {


  def this() = this(0L, "")

  def this(name: String) = this(0L, name)

  lazy val comments: OneToMany[Comment] = SharedSchema.channelToComment.left(this)

  def addComment(comment: Comment) = {
    comment.channelId = this.id
    comment.save()
  }

}

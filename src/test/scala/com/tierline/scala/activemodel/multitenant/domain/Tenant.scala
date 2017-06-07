package com.tierline.scala.activemodel.multitenant.domain

import com.tierline.scala.activemodel.{ActiveModel, Repository}
import org.squeryl.PrimitiveTypeMode._

object Tenant extends Repository[Tenant] {
  def findByName(name: String): Option[Tenant] = {
    find(t => t.name === name).headOption
  }

}

case class Tenant(
  var id: Long,
  var tenantId: String,
  var name: String) extends ActiveModel {

  def this(tenantId: String, name: String) {
    this(0L, tenantId, name)
  }

}


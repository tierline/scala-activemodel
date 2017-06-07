package com.tierline.scala.activemodel.singletenant.domain

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant._
import org.squeryl.PrimitiveTypeMode._

object SeparateTenant extends Repository[SeparateTenant] with MultiTenantFinder {
  def findById(key: String): Option[MultiTenant] = {
    find(t => t.tenantSchemaName === key).headOption match {
      case Some(t) => Some(t.multiTenant)
      case None => None
    }
  }
}

case class SeparateTenant(var id: Long, var tenantSchemaName: String) extends ActiveModel {

  def this(name: String) {
    this(0, name)
  }

  def multiTenant: MultiTenant = MultiTenant("", "", tenantSchemaName)
}

package com.tierline.scala.activemodel.domain

import scala.util.DynamicVariable
import org.squeryl.PrimitiveTypeMode._
import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant._

object Tenant extends Repository[Tenant] with MultiTenantFinder {

  def findById(key: String): Option[MultiTenant] = {
    find(t => t.tenantSchemaName === key).toSeq.headOption match {
      case Some(t) => Some(t.multiTenant)
      case None => None
    }
  }
}

case class Tenant(var id: Long, var tenantSchemaName: String) extends ActiveModel {

  def this(name: String) {
    this(0, name)
  }

  def multiTenant: MultiTenant = MultiTenant("", "", tenantSchemaName)
}

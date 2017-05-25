package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.domain.Tenant
import org.squeryl.Session

import scala.util.DynamicVariable

object SharedSchemaTransactionManager {
  var _tenant: Option[Tenant] = None

  def newSession: Option[Session] = {
    println("new session")
    None
  }

  def tenant_=(tenant: Tenant) = this._tenant = Some(tenant)

  def tenant = _tenant

}

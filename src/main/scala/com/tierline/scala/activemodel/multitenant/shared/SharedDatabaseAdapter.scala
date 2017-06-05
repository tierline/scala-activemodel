package com.tierline.scala.activemodel.multitenant.shared

import org.squeryl.internals.DatabaseAdapter

import scala.collection.mutable.ListBuffer

trait SharedDatabaseAdapter
  extends DatabaseAdapter
    with InsertAdapter
    with UpdateAdapter
    with DeleteAdapter
    with SelectAdapter {

  def tenantIdColumnName = Multitenancy.tenantIdColumnName

  protected def addHeadTenantParams(tenantId: String, params: Iterable[AnyRef]): Iterable[AnyRef] = {
    val buffer = new ListBuffer[AnyRef]()
    params.foreach(p => buffer += p)
    tenantId +=: buffer
    buffer
  }

  protected def addTailTenantParams(tenantId: String, params: Iterable[AnyRef]): Iterable[AnyRef] = {
    val buffer = new ListBuffer[AnyRef]()
    params.foreach(p => buffer += p)
    buffer += tenantId
    buffer
  }

}

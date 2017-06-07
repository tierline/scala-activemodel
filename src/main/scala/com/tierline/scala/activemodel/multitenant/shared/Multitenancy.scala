package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.{ActiveModel, ActiveModelBase}
import org.squeryl.Table
import org.squeryl.internals.StatementWriter

import scala.collection.mutable
import scala.util.DynamicVariable


object Multitenancy {

  private var _tenantColumnName = "tenantId"

  def tenantIdColumnName = _tenantColumnName

  def tenantIdColumnName_=(name: String) = this._tenantColumnName = name

  private val EMPTY_TENANT = "EMPTY_TENANT"

  private val hookTables = mutable.HashSet[Table[ActiveModel]]()

  val currentTenant = new DynamicVariable[String]("EMPTY_TENANT")

  def apply(tables: Table[_]*): Unit = {
    tables.foreach(t => hookTables.add(t.asInstanceOf[Table[ActiveModel]]))
  }

  def hook(table: Table[_]): Boolean = {
    hookTables.contains(table.asInstanceOf[Table[ActiveModel]])
  }

  def hook: Boolean = currentTenant.value != EMPTY_TENANT

  def hook(sw: StatementWriter): Boolean = {
    hookTables.foreach { t =>
      if (sw.toString().contains(t.prefixedName)) {
        return true
      }
    }
    false
  }

  def currentTenantValue: Option[String] = if (isEmpty) None else Some(this.currentTenant.value)

  def isEmpty: Boolean = this.currentTenant.value.equals(EMPTY_TENANT)

}

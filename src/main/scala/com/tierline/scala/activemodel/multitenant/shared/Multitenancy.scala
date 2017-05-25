package com.tierline.scala.activemodel.multitenant.shared

import org.squeryl.Table
import org.squeryl.internals.StatementWriter

import scala.util.DynamicVariable


object Multitenancy {

  private val EMPTY_TENANT = "EMPTY_TENANT"

  private var hookTables = Set[Table[_]]()

  val currentTenant = new DynamicVariable[String]("EMPTY_TENANT")

  def hook[T](table: Table[T]): Boolean = {
    hookTables.contains(table)
  }

  def apply[T](table: Table[T]): Unit = {
    this.hookTables = hookTables + table
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

}

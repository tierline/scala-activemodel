package com.tierline.scala.activemodel.multitenant

import com.mchange.v2.c3p0.ComboPooledDataSource
import scala.util.DynamicVariable
import javax.sql.DataSource
import scala.tools.scalap.scalax.util.StringUtil

trait MultiTenantFinder {

  def findById(key: String): Option[MultiTenant]
}

object MultiTenant {
  var agent: Option[MultiTenantFinder] = None

  def apply(agent: MultiTenantFinder) {
    this.agent = Some(agent)
  }

  val keyHolder = new DynamicVariable[String]("")

  def key: String = keyHolder.value

  def findById(id: String): Option[MultiTenant] = agent match {
    case Some(a) => a.findById(id)
    case None => throw new IllegalStateException(s"is not set tenant agent")
  }

  def scope(key: String)(fanc: => Unit) = {
    keyHolder.withValue(key) {
      fanc
    }
  }

  def isScope: Boolean = !keyHolder.value.isEmpty()

  def outScope(f: => Unit) = {
    keyHolder.withValue("") {
      f
    }
  }

}

case class MultiTenant(user: String, password: String, schema: String) {

  def scope(fanc: => Unit) = {
    MultiTenant.scope(schema) {
      fanc
    }
  }
}

package com.tierline.scala.activemodel.multitenant


import scala.util.DynamicVariable

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

  def scope(key: String)(func: => Unit) = {
    keyHolder.withValue(key) {
      func
    }
  }

  def isScope: Boolean = !keyHolder.value.isEmpty

  def outScope(f: => Unit) = {
    keyHolder.withValue("") {
      f
    }
  }

}

case class MultiTenant(user: String, password: String, schema: String) {

  def scope(func: => Unit) = {
    MultiTenant.scope(schema) {
      func
    }
  }
}

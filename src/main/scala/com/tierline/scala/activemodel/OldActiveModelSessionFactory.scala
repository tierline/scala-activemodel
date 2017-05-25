package com.tierline.scala.activemodel

import org.squeryl.Session
import org.squeryl.SessionFactory
import grizzled.slf4j.Logging
import com.tierline.scala.activemodel.multitenant.MultitenancyActiveModelSchema
import com.tierline.scala.activemodel.multitenant.MultiTenant

object OldActiveModelSessionFactory extends Logging {

  var multiTenancy = false

  var defaultSchema: Option[ActiveModelSchema] = None

  var multiTenantSchema: Option[MultitenancyActiveModelSchema] = None

  def defaultSession: () => Session = { () =>
    defaultSchema.getOrElse(throw new IllegalAccessException("デフォルトスキーマが設定されていません")).createSession
  }

  def multiSession: () => Session = { () =>
    //マルチテナントへのアクセスかどうかをスコープ内かどうかで判断している
    //ちょっと微妙…20150528
    if (MultiTenant.isScope) {
      multiTenantSchema.getOrElse(throw new IllegalAccessException("マルチテナントスキーマが設定されていません")).createSession
    } else {
      defaultSession()
    }
  }

  def setDefault(schema: ActiveModelSchema) {
    defaultSchema = Some(schema)
    SessionFactory.concreteFactory = Some(defaultSession)
  }

  def setMultiTenant(schema: MultitenancyActiveModelSchema) {
    multiTenantSchema = Some(schema)
    multiTenancy = true
    SessionFactory.concreteFactory = Some(multiSession)
  }

  def clear() {
    Session.currentSessionOption match {
      case Some(s) => {
        s.cleanup
        s.close
      }
      case _ =>
    }
    debug("Clear Schema from ActiveRecoreSessionFactory")
    defaultSchema = None
    multiTenantSchema = None
  }
}

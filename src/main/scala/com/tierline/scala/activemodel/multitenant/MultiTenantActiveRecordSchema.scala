package com.tierline.scala.activemodel.multitenant

import scala.collection.mutable.HashMap
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.tierline.scala.activemodel.ActiveModelSchema
import com.tierline.scala.activemodel.ActiveModelSessionFactory

trait MultiTenantActiveModelSchema extends ActiveModelSchema {

  override def create() = {
    throw new IllegalAccessException("マルチテナントスキーマではcreate(tenants:Seq[String]())メソッドを利用してください")
  }

  override def drop() = {
    throw new IllegalAccessException("マルチテナントスキーマではdrop(tenants:Seq[String]())メソッドを利用してください")
  }

  def create(tenants: Seq[MultiTenant]) {
    tenants.foreach { tenant =>
      tenant.scope {
        debug(s"Creating Schema[${tenant.schema}]")
        transaction(newSession) {
          super.create()
        }
      }
    }
  }

  def drop(tenants: Seq[MultiTenant]) {
    tenants.foreach { tenant =>
      tenant.scope {
        debug(s"Droping Schema[${tenant.schema}]")
        transaction(newSession) {
          super.drop()
        }
      }
    }
  }

  val dataSourceCache: HashMap[String, ComboPooledDataSource] = HashMap()

  protected def loadDataSource(key: String, url: String, driver: String): ComboPooledDataSource = {
    MultiTenant.findById(key) match {
      case Some(m) => createDataSource(m, url, driver)
      case None => throw new IllegalArgumentException("指定されたマルチテナントは存在しません")
    }
  }

  protected def createDataSource(m: MultiTenant, url: String, driver: String): ComboPooledDataSource = {
    Class.forName(driver)
    val cpds = new ComboPooledDataSource
    cpds.setDriverClass(driver)
    val rUrl = url.replace("${schema}", m.schema)
    cpds.setJdbcUrl(rUrl)
    cpds.setUser(m.user)
    cpds.setPassword(m.password)
    cpds
  }

  override def createSession: Session = {
    val key = MultiTenant.key
    val d = dataSourceCache.get(key) match {
      case Some(d) => d
      case None => {
        val newSource = loadDataSource(key, databaseAdapter.url, databaseAdapter.driver)
        dataSourceCache += (key -> newSource)
        logger.info(s"Setting new DataSource:$key")
        newSource
      }
    }
    debug(s"Create Session of  ${d.getJdbcUrl}")
    Session.create(d.getConnection, databaseAdapter.adapter)
  }

  override def init() {
    ActiveModelSessionFactory.setMultiTenant(this)
  }

}

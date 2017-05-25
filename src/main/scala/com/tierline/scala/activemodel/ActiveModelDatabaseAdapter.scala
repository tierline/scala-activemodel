package com.tierline.scala.activemodel

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.Config
import grizzled.slf4j.Logging
import org.squeryl.adapters.{H2Adapter, MySQLAdapter}
import org.squeryl.internals.DatabaseAdapter


abstract class ActiveModelDatabaseAdapter(conf: Config, val driver: String) extends Logging {
  val url: String = createUrl
  val user: String = conf.getString("user")
  val password: String = conf.getString("password")
  val dataSource: ComboPooledDataSource = createDateSource(url, driver, user, password)
  val adapter: DatabaseAdapter

  protected def createUrl: String = conf.getString("url")

  def createDateSource(url: String, driver: String, user: String = "", password: String = ""): ComboPooledDataSource = {
    logger.info("Creating connection with c3po connection pool")
    Class.forName(driver)
    val ds = new ComboPooledDataSource
    ds.setJdbcUrl(url)
    ds.setDriverClass(driver)
    ds.setUser(user)
    ds.setPassword(password)
    ds

  }

  def nativeQueryAdapter: NativeQueryAdapter = {
    adapter match {
      case _: MySQLAdapter => MySqlDBAdapter
      case _: H2Adapter => H2DBAdapter
      case _: MSSQLServer => MSSqlAdapter
      case _ => null
    }
  }
}
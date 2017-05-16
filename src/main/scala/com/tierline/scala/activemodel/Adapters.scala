package com.tierline.scala.activemodel

import org.squeryl.adapters._
import org.squeryl.internals.DatabaseAdapter
import javax.sql.DataSource

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.Config
import grizzled.slf4j.Logging
import org.apache.commons.dbcp.BasicDataSource

trait ActiveModelDatabaseAdapterSupport extends Logging {
  val url: String
  val user: String
  val password: String
  val driver: String
  val adapter: DatabaseAdapter
  val dataSource: ComboPooledDataSource

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

abstract class ActiveModelDatabaseAdapter(conf: Config, val driver: String) extends ActiveModelDatabaseAdapterSupport {
  val url: String = createUrl
  val user: String = conf.getString("user")
  val password: String = conf.getString("password")
  val dataSource: ComboPooledDataSource = createDateSource(url, driver, user, password)

  protected def createUrl: String = conf.getString("url")
}

object SequentialActiveModelDatabaseAdapter {
  var seq: Long = 0

  def next: Long = {
    seq.synchronized {
      seq = seq + 1
      seq
    }
  }
}

abstract class SequentialActiveModelDatabaseAdapter(conf: Config, driver: String) extends ActiveModelDatabaseAdapter(conf, driver) {
  override def createUrl: String = {
    conf.getString("url").replace("${schema}", s"db-${SequentialActiveModelDatabaseAdapter.next}")
  }

}


case class MariaDB(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.mariadb.jdbc.Driver") {
  val adapter = new MySQLInnoDBAdapter
}

case class H2(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.h2.Driver") {
  val adapter = new H2Adapter
}

case class H2Concurrent(conf: Config) extends SequentialActiveModelDatabaseAdapter(conf, "org.h2.Driver") {
  val adapter = new H2Adapter
}

case class DB2(conf: Config) extends ActiveModelDatabaseAdapter(conf, "com.ibm.db2.jcc.DB2Driver") {
  val adapter = new DB2Adapter
}

case class Derby(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.apache.derby.jdbc.EmbeddedDriver") {
  val adapter = new DerbyAdapter
}

case class MSSQLServer(conf: Config) extends ActiveModelDatabaseAdapter(conf, "sqljdbc.jar, sqljdbc4.ja") {
  type MSSQLAdapter = org.squeryl.adapters.MSSQLServer
  val adapter = new MSSQLAdapter
}

case class MySql(conf: Config) extends ActiveModelDatabaseAdapter(conf, "com.mysql.jdbc.Driver") {
  val adapter = new MySQLAdapter
}

case class MySqlInnoDB(conf: Config) extends ActiveModelDatabaseAdapter(conf, "com.mysql.jdbc.Driver") {
  val adapter = new MySQLInnoDBAdapter
}

case class Oracle(conf: Config) extends ActiveModelDatabaseAdapter(conf, "oracle.jdbc.driver.OracleDriver") {
  val adapter = new OracleAdapter
}

case class PostgreSql(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.postgresql.Driver") {
  val adapter = new PostgreSqlAdapter
}

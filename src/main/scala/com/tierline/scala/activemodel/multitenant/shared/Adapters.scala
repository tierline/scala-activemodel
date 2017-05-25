package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.ActiveModelDatabaseAdapter
import com.typesafe.config.Config

case class H2(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.h2.Driver") {
  val adapter = new H2Adapter
}


//case class MariaDB(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.mariadb.jdbc.Driver") {
//  val adapter = new MySQLInnoDBAdapter
//}


//
//case class DB2(conf: Config) extends ActiveModelDatabaseAdapter(conf, "com.ibm.db2.jcc.DB2Driver") {
//  val adapter = new DB2Adapter
//}
//
//case class Derby(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.apache.derby.jdbc.EmbeddedDriver") {
//  val adapter = new DerbyAdapter
//}
//
//case class MSSQLServer(conf: Config) extends ActiveModelDatabaseAdapter(conf, "sqljdbc.jar, sqljdbc4.ja") {
//  type MSSQLAdapter = org.squeryl.adapters.MSSQLServer
//  val adapter = new MSSQLAdapter
//}
//
//case class MySql(conf: Config) extends ActiveModelDatabaseAdapter(conf, "com.mysql.jdbc.Driver") {
//  val adapter = new MySQLAdapter
//}
//
//case class MySqlInnoDB(conf: Config) extends ActiveModelDatabaseAdapter(conf, "com.mysql.jdbc.Driver") {
//  val adapter = new MySQLInnoDBAdapter
//}
//
//case class Oracle(conf: Config) extends ActiveModelDatabaseAdapter(conf, "oracle.jdbc.driver.OracleDriver") {
//  val adapter = new OracleAdapter
//}
//
//case class PostgreSql(conf: Config) extends ActiveModelDatabaseAdapter(conf, "org.postgresql.Driver") {
//  val adapter = new PostgreSqlAdapter
//}

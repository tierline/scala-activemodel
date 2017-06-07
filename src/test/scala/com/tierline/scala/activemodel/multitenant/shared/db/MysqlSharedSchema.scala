package com.tierline.scala.activemodel.multitenant.shared.db

import com.tierline.scala.activemodel.multitenant.shared.{MySql, SharedSchema}

object MysqlSharedSchema extends SharedSchema {
  override def configName = "mysql-database.conf"

  SharedSchema.currentSchema = this
  databaseAdapter = MySql(dbConf)
}

package com.tierline.scala.activemodel.multitenant.shared.db

import com.tierline.scala.activemodel.multitenant.shared.{H2, SharedSchema}

object H2SharedSchema extends SharedSchema {
  override def configName = "h2-database.conf"

  SharedSchema.currentSchema = this
  databaseAdapter = H2(dbConf)

}

package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.ActiveModelSchema
import com.tierline.scala.activemodel.multitenant.domain._
import com.typesafe.config.ConfigFactory
import org.squeryl.Session
import org.squeryl.PrimitiveTypeMode._

object SharedSchema extends ActiveModelSchema {


  override def sessionFactory = { () =>
    Session.create(
      this.databaseAdapter.dataSource.getConnection,
      this.databaseAdapter.adapter)
  }

  val config = ConfigFactory.load("database.conf")

  val dbConf = config.getConfig("shared")

  databaseAdapter = H2(dbConf)

  val tenant = Table[Tenant]

  val channel = Table[Channel]

  val comment = Table[Comment]

  val channelToComment = oneToManyRelation(channel, comment).via((ch, co) => ch.id === co.channelId)

  Multitenancy(channel, comment)

  //
  //
  //  val keyValue = Table[KeyValue]

  //  this._tenantTable = Table[SharedTenant]

  //  var _tenantTable: Table[SharedTenant] = _
  //
  //  def tenantTable_=(table: Table[SharedTenant]): Unit = {
  //    _tenantTable = table
  //  }
  //
  //  def tenantTable = _tenantTable
}

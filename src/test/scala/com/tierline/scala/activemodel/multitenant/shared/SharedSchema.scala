package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.ActiveModelSchema
import com.tierline.scala.activemodel.multitenant.domain._
import com.typesafe.config.ConfigFactory
import org.squeryl.PrimitiveTypeMode._

object SharedSchema {
  def comment = currentSchema.comment

  def channel = currentSchema.channel


  def channelToComment = currentSchema.channelToComment


  var currentSchema: SharedSchema = _
}

trait SharedSchema extends ActiveModelSchema {

  def configName: String

  val config = ConfigFactory.load(configName)

  val dbConf = config.getConfig("shared")

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

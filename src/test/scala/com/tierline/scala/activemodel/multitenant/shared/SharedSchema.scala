package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant.shared.H2
import com.tierline.scala.activemodel.multitenant.domain.{MultitenancyModel, _}
import com.typesafe.config.ConfigFactory
import org.squeryl.{Session, Table}
import org.squeryl.PrimitiveTypeMode._

import scala.util.DynamicVariable


object SharedSchema extends ActiveModelSchema {


  //  override def insert[K](table: Table[ActiveModelBase[K]], e: ActiveModelBase[K]) = {
  //    e match {
  //      //      case tenantModel: MultitenancyModel => {
  //      //        tenantModel.tenantId = currentTenant.value
  //      //        table.insert(tenantModel)
  //      //      }
  //      case tenantModel: Channel => {
  //        //        tenantModel.tenantId = currentTenant.value
  //        table.insert(tenantModel)
  //      }
  //      case _ => {
  //        table.insert(e)
  //      }
  //    }
  //  }

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


  Multitenancy(channel)

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

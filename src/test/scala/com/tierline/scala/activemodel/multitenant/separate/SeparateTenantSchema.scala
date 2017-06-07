package com.tierline.scala.activemodel.multitenant.separate

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.multitenant.domain._
import com.tierline.scala.activemodel.multitenant.MultitenancyActiveModelSchema
import com.tierline.scala.activemodel.singletenant.domain.{Cart, Goods, KeyValue, SeparateTenant}
import com.typesafe.config.ConfigFactory
import org.squeryl.PrimitiveTypeMode._

object SeparateCentralSchema extends ActiveModelSchema {

  //  val config = ConfigFactory.load("database.conf")
  //
  //  val dbConf = config.getConfig("database")
  //
  //  databaseAdapter = H2(dbConf)
  //
  //  val tenant = Table[SeparateTenant]

}

object SeparateTenantSchema extends MultitenancyActiveModelSchema {

  //  val config = ConfigFactory.load("database.conf")
  //
  //  val dbConf = config.getConfig("separate")
  //
  //  databaseAdapter = H2(dbConf)
  //
  //  val goods = Table[Goods]
  //
  //  val cart = Table[Cart]
  //
  //  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)
  //
  //  val keyValue = Table[KeyValue]

}

package com.tierline.scala.activemodel.multitenant

import org.squeryl.PrimitiveTypeMode._

import com.tierline.scala.activemodel._
import com.tierline.scala.activemodel.domain.Cart
import com.tierline.scala.activemodel.domain.Goods
import com.tierline.scala.activemodel.domain.KeyValue
import com.tierline.scala.activemodel.domain.Tenant
import com.typesafe.config.ConfigFactory

object CentralSchema extends ActiveModelSchema {

  val config = ConfigFactory.load("database.conf")

  val dbConf = config.getConfig("database")

  databaseAdapter = H2(dbConf)

  val tenant = Table[Tenant]

}

object MultiTenantSchema extends MultiTenantActiveModelSchema {

  val config = ConfigFactory.load("database.conf")

  val dbConf = config.getConfig("multitenant")

  databaseAdapter = H2(dbConf)

  val goods = Table[Goods]

  val cart = Table[Cart]

  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)

  val keyValue = Table[KeyValue]

}

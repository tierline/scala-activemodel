package com.tierline.scala.activemodel.domain

import org.squeryl.PrimitiveTypeMode._
import com.tierline.scala.activemodel._
import com.typesafe.config.ConfigFactory


abstract class Schema(configName: String) extends ActiveModelSchema {

  val config = ConfigFactory.load("database.conf")

  val dbConf = config.getConfig(configName)

  databaseAdapter = H2Concurrent(dbConf)

  val goods = Table[Goods]

  val cart = Table[Cart]

  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)

  val keyValue = Table[KeyValue]
}

object TestSchema extends Schema("database")


object EnvironmentConfigTestSchema extends ActiveModelSchema {

  val goods = Table[Goods]

  val cart = Table[Cart]

  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)

  val keyValue = Table[KeyValue]
}

package com.tierline.scala.activemodel.domain

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import com.tierline.scala.activemodel._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object TestSchema extends ActiveModelSchema {

  val config = ConfigFactory.load("database.conf")

  val dbConf = config.getConfig("database")

  databaseAdapter = H2(dbConf)

  val goods = Table[Goods]

  val cart = Table[Cart]

  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)

  val keyValue = Table[KeyValue]

}

object EnviromentConfigTestSchema extends ActiveModelSchema {

  val goods = Table[Goods]

  val cart = Table[Cart]

  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)

  val keyValue = Table[KeyValue]
}

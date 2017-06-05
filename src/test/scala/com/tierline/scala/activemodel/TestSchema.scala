package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.singletenant.domain._
import com.typesafe.config.ConfigFactory
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session


abstract class Schema(configName: String) extends ActiveModelSchema {

  val config = ConfigFactory.load("database.conf")

  val dbConf = config.getConfig(configName)

  databaseAdapter = H2(dbConf)

  val goods = Table[Goods]

  val cart = Table[Cart]

  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)

  val keyValue = Table[KeyValue]
}

object TestSchema extends Schema("database") {
  override def sessionFactory = { () =>
    Session.create(
      this.databaseAdapter.dataSource.getConnection,
      this.databaseAdapter.adapter)
  }
}


object EnvironmentConfigTestSchema extends ActiveModelSchema {

  val goods = Table[Goods]

  val cart = Table[Cart]

  val cartToGoods = oneToManyRelation(cart, goods).via((c, g) => c.id === g.cartId)

  val keyValue = Table[KeyValue]

  override def sessionFactory = { () =>
    Session.create(
      this.databaseAdapter.dataSource.getConnection,
      this.databaseAdapter.adapter)
  }

}

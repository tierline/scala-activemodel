package com.tierline.scala.activemodel.singletenant.domain

import com.tierline.scala.activemodel._
import org.squeryl.dsl.ManyToOne

object Goods extends Repository[Goods]

case class Goods(
  var id: Long,
  var name: String,
  var price: Long) extends ActiveModel {

  var cartId: Long = _

  def this() = this(0L, "", 0L)

  def this(cartId: Long) {
    this()
    this.cartId = cartId
  }

  lazy val cart: ManyToOne[Cart] = TestSchema.cartToGoods.right(this)
}


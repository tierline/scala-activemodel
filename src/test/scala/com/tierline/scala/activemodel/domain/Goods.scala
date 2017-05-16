package com.tierline.scala.activemodel.domain

import org.squeryl.dsl.ManyToOne

import com.tierline.scala.activemodel._

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

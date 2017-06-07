package com.tierline.scala.activemodel.singletenant.domain

import com.tierline.scala.activemodel._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.OneToMany

object Cart extends Repository[Cart] {

  def countByName(name: String): Long = {
    val r = from(repo)(e =>
      where(e.name === name)
        compute (count(e.id)))
    debug(r.statement)
    r
  }
}

case class Cart(
  var id: Long,
  var name: String,
  var size: Long) extends ActiveModel with SaveBeforeAfterSupport {

  def this() = this(0L, "", 0L)

  def this(name: String, size: Long) = this(0L, name, size)

  lazy val goods: OneToMany[Goods] = TestSchema.cartToGoods.left(this)

  beforeSave {
    println("before save")
  }

}

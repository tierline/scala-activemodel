package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.domain._
import org.squeryl.PrimitiveTypeMode._

class RepositoryTest extends TestSuite {

  def insert(size: Int) {
    for (i <- 0 until size) new Cart("Note" + i, 1000 * i).save()
  }

  test("Reset auto inclument") {
    val c1 = new Cart().create()
    val firstId = c1.id;
    val c2 = new Cart().create()
    assert(c2.id == firstId + 1)
    Cart.deleteAll()
    Cart.resetAutoIncrement()

    val c3 = new Cart().create()
    assert(c3.id == 1)
  }
  test("Delete All") {
    Cart.deleteAll()

    assert(Cart.countAll == 0)
  }

  test("Delete All By Condition") {

    insert(10)
    Cart.deleteAll(c => (c.name like "Note1%") or (c.name === "Note0"))

    val result = Cart.countAll
    assert(result == 8, "result = " + result)
  }

  test("Find by id") {
    val id = new Cart("Note", 1000).create.id

    Cart.findById(id) match {
      case Some(g) => assert(true)
      case None => assert(false)
    }
  }
  test("Count entities") {
    Cart.deleteAll()
    val size: Int = 20
    insert(size)

    assert(Cart.countAll == size)
  }

  test("Count entities by condition") {

    Cart.deleteAll()
    val size: Int = 20
    insert(size)

    val name = Cart.first.get.name
    val result = Cart.countBy(c => c.name === name).toInt
    assert(result == 1)
  }

  test("Exsits entity") {
    Cart.deleteAll()
    val size: Int = 20
    insert(size)

    val id = Cart.first.get.id
    assert(true == Cart.exists(id))

    val notExsitsId = 0
    assert(false == Cart.exists(notExsitsId))

  }

  test("Count by conditions") {
    Cart.deleteAll()
    val size: Int = 20
    insert(size)

    val count = Cart.countByName(Cart.first.get.name)

    assert(count == 1)
  }

  test("Find First Objects") {
    Goods.deleteAll()
    Cart.deleteAll()
    val size: Int = 20
    insert(size)

    Cart.first match {
      case Some(v) => assert(true)
      case _ => assert(false)
    }

  }

  test("Find") {

    val size: Int = 20

    Cart.deleteAll()
    insert(size)
    assert(Cart.countAll == size)

    val id: Long = Cart.first match {
      case Some(value) => value.id
      case None => {
        assert(false)
        0
      }

    }

    var sizeResult = 0;
    {
      val result1 = Cart.find(e => (e.id === id) and (e.name like "%Note%"))
      sizeResult = result1.size
    }
    assert(sizeResult == 1, "result = " + sizeResult)
  }

  test("Getting All Objects") {
    Cart.deleteAll()
    val size: Int = 20
    insert(size)

    val all = Cart.all
    assert(all.size == size)
  }

  test("Save Goods of child object by associate") {
    Goods.deleteAll()

    var cart = new Cart(0L, "", 100).create
    cart.goods.associate(new Goods(0L, "", 100))

    assert(Goods.countAll == 1)
    val seq = cart.goods.toSeq
    assert(seq.size == 1)
  }

  test("Save Goods of child object by assign") {
    Goods.deleteAll()
    var g = new Goods(0L, "", 100)
    new Cart().create.goods.assign(g)
    g.save
    assert(Goods.countAll == 1)

    Goods.deleteAll()
    var g2 = new Goods(0L, "", 100)
    g2.cart.assign(new Cart().create)
    g.save
    assert(Goods.countAll == 1)
  }

  test("Fetch object") {
    Goods.deleteAll()
    Cart.deleteAll()
    insert(17)
    assert(Cart.countAll == 17)

    assert(Cart.fetch(1, 10).size == 10)
    assert(Cart.fetch(2, 10).size == 7)
    assert(Cart.fetch(3, 10).size == 0)
    assert(Cart.fetch(0, 10).size == 10)
  }

  test("Fetch object by condition") {
    Goods.deleteAll()
    Cart.deleteAll()
    insert(17)
    assert(Cart.countAll == 17)

    assert(Cart.fetch(c => c.name === "Note1")(1, 10).size == 1)
  }

  test("Fetch object by order by") {
    Goods.deleteAll()
    Cart.deleteAll()
    insert(17)
    import org.squeryl.PrimitiveTypeMode._
    assert(Cart.countAll == 17)

    assert(Cart.find(c => 1 === 1,
      c => c.size desc).head.size == 16000)
  }

}
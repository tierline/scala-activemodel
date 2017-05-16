package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.domain.{Goods, KeyValue}
import org.squeryl.PrimitiveTypeMode._

class StringKeyRepositoryTest extends TestSuite {

  override def beforeAll = {
    super.beforeAll()
    KeyValue.deleteAll()
  }

  def insert(size: Int) {
    for (i <- 0 until size) new KeyValue("Key-" + i, "value-" + (1000 * i)).save()
  }

  test("Delete All") {
    KeyValue.deleteAll()

    assert(KeyValue.countAll == 0)
  }

  test("Delete All By Condition") {

    insert(10)
    KeyValue.deleteAll(c => (c.id like "Key-0") or (c.id === "Key-1"))

    val result = KeyValue.countAll
    assert(result == 8, "result = " + result)
  }

  test("Find by id") {
    val id = new KeyValue("Key", "1000").create().id

    KeyValue.findById(id) match {
      case Some(g) => assert(true)
      case None => assert(false)
    }
  }
  test("Count entities") {
    KeyValue.deleteAll()
    val size: Int = 20
    insert(size)

    assert(KeyValue.countAll == size)
  }

  test("Count entities by condition") {

    KeyValue.deleteAll()
    val size: Int = 20
    insert(size)

    val name = KeyValue.first.get.id
    var result = KeyValue.countBy(c => c.id === name).toInt
    assert(result == 1)
  }

  test("Exsits entity") {
    KeyValue.deleteAll()
    val size: Int = 20
    insert(size)

    val id = KeyValue.first.get.id
    assert(true == KeyValue.exists(id))

    val notExsitsId = "0"
    assert(false == KeyValue.exists(notExsitsId))

  }

  test("Count by conditions") {
    KeyValue.deleteAll()
    val size: Int = 20
    insert(size)

    val count = KeyValue.countByValue(KeyValue.first.get.value)

    assert(count == 1)
  }

  test("Find First Objects") {
    Goods.deleteAll()
    KeyValue.deleteAll()
    val size: Int = 20
    insert(size)

    KeyValue.first match {
      case Some(v) => assert(true)
      case _ => assert(false)
    }

  }

  test("Find") {

    val size: Int = 20

    KeyValue.deleteAll()
    insert(size)
    assert(KeyValue.countAll == size)

    val id: String = KeyValue.first.get.id

    var sizeResult = 0;
    {
      val result1 = KeyValue.find(e => (e.id === id))
      sizeResult = result1.size
    }
    assert(sizeResult == 1)
  }

  test("Getting All Objects") {
    KeyValue.deleteAll()
    val size: Int = 20
    insert(size)

    val all = KeyValue.all
    assert(all.size == size)
  }

  test("Fetch object") {
    Goods.deleteAll()
    KeyValue.deleteAll()
    insert(17)
    assert(KeyValue.countAll == 17)

    assert(KeyValue.fetch(1, 10).size == 10)
    assert(KeyValue.fetch(2, 10).size == 7)
    assert(KeyValue.fetch(3, 10).size == 0)
    assert(KeyValue.fetch(0, 10).size == 10)
  }

  test("Fetch object by condition") {
    Goods.deleteAll()
    KeyValue.deleteAll()
    insert(17)
    assert(KeyValue.countAll == 17)

    val result = KeyValue.fetch(c => c.id === "Key-1")(1, 10).size
    assert(result == 1)
  }

  test("Fetch object by order by") {
    Goods.deleteAll()
    KeyValue.deleteAll()
    insert(10)
    import org.squeryl.PrimitiveTypeMode._
    assert(KeyValue.countAll == 10)

    assert(KeyValue.find(
      c => 1 === 1,
      c => c.value desc)
      .head.value == "value-9000")
  }

}
package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.domain.Cart
import org.squeryl.PrimitiveTypeMode.transaction
import NativeQuery._
import com.tierline.scala.activemodel.domain.TestSchema

class NativeQueryTest extends TestSuite {

  def insert(size: Int) {
    for (i <- 0 until size) new Cart("Note" + i, 1000 * i).save
  }

  before {
    Cart.deleteAll()
    insert(10)
  }

  test("get single obj") {
    val sql = "select * from Cart"
    val cart = NativeQuery(TestSchema).querySingle(sql) { rs =>
      new Cart(rs.getLong("id"), rs.getString("name"), rs.getLong("size"))
    }

    cart match {
      case Some(value) => assert(true)
      case None => assert(false)
    }
  }

  test("get single obj by param") {
    val sql = "select * from Cart where name like %name"
    val params: Map[String, Seq[String]] = Map("name" -> Seq("%Note%"))

    val cart = NativeQuery(TestSchema).querySingle(sql, params) { rs =>
      new Cart(rs.getLong("id"), rs.getString("name"), rs.getLong("size"))
    }

    cart match {
      case Some(value) => assert(true)
      case None => assert(false)
    }
  }

  test("get list") {

    val sql = "select * from Cart"
    val list = NativeQuery(TestSchema).queryEach(sql) { rs =>
      new Cart(rs.getLong("id"), rs.getString("name"), rs.getLong("size"))
    }
    assert(list.size === 10)
  }

  test("get list by params") {

    val sql = "select * from Cart where name like %name"
    val params = Map("name" -> Seq("%Note%"))
    val list = NativeQuery(TestSchema).queryEach(sql, params) { rs =>
      new Cart(rs.getLong("id"), rs.getString("name"), rs.getLong("size"))
    }
    assert(list.size === 10)
  }

  test("get list by params comprising the seq") {

    val sql = "select * from Cart where name in (%name)"
    val params = Map("name" -> Seq("Note1", "Note2", "Note3", "Note4", "Note5"))
    val list = NativeQuery(TestSchema).queryEach(sql, params) { rs =>
      new Cart(rs.getLong("id"), rs.getString("name"), rs.getLong("size"))
    }
    assert(list.size === 5)
  }

  test("update") {
    Cart.deleteAll()
    insert(10)
    val sql = "update Cart set name = 'test'"
    val count = NativeQuery(TestSchema).update(sql)
    assert(count === 10)

    Cart.all.foreach(c => assert(c.name === "test"))
  }

  test("execute statement") {
    val sql = "SHOW TABLES;"
    val results = NativeQuery(TestSchema).execute(sql) { rs =>
      println(s"${rs.getString(1)} ${rs.getString(2)}")
    }
    assert(results != null)
  }

}

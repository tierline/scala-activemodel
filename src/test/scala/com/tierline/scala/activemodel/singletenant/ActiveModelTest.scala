package com.tierline.scala.activemodel.singletenant

import com.tierline.scala.activemodel.TestSuite
import com.tierline.scala.activemodel.singletenant.domain._


class ActiveModelTest extends TestSuite {

  def insert(size: Int) {
    for (i <- 0 until size) new Cart("Note" + i, 1000 * i).save()
  }

  test("Save") {
    val isSaved = new Cart("Note", 1000).save()

    assert(isSaved, "didn't saved")
  }

  test("Delete") {
    val savedEntity = new Cart("Note", 1000).create()
    val id = savedEntity.id
    savedEntity.delete()

    Cart.findById(id) match {
      case Some(g) => fail()
      case None => succeed
    }
  }

  test("fail Delete") {
    val savedEntity = new Cart("Note", 1000)
    val id = 12
    assert(false === savedEntity.delete())

    Cart.findById(id) match {
      case Some(g) => fail()
      case None => succeed
    }
  }

  test("Update") {
    val savedEntity = new Cart("Note", 1000).create()
    val id = savedEntity.id
    savedEntity.name = "new name"
    savedEntity.size = 2000
    savedEntity.update()

    Cart.findById(id) match {
      case Some(g) =>
        assert(g.name == "new name")
        assert(g.size == 2000)
      case None => fail()
    }
  }

  test("Update partial field") {
    import org.squeryl.PrimitiveTypeMode._

    val savedEntity = new Cart("Note", 1000).create()
    val id = savedEntity.id
    savedEntity.updatePartial(c => c.name := "new name")

    Cart.findById(id) match {
      case Some(g) =>
        assert(g.name == "new name")
      case None => fail()
    }
  }

}
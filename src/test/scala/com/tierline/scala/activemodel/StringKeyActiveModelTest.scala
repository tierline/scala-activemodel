package com.tierline.scala.activemodel

import com.tierline.scala.activemodel.domain._

class StringKeyActiveModelTest extends TestSuite {

  override def beforeAll() = {
    super.beforeAll()
    KeyValue.deleteAll()
  }

  def insert(size: Int) {
    for (i <- 0 until size) new KeyValue("Key-" + i, "value-" + (1000 * i)).save
  }

  test("Save") {

    val isSaved = new KeyValue("Key-save", "1000").save

    assert(isSaved, "didn't saved")

  }

  test("Delete") {
    val savedEntity = new KeyValue("Key-delete", "1000").create
    val id = savedEntity.id
    savedEntity.delete

    KeyValue.findById(id) match {
      case Some(g) => assert(false)
      case None => assert(true)
    }
  }

  test("Update") {
    val savedEntity = new KeyValue("Key-update", "1000").create
    val id = savedEntity.id
    savedEntity.value = "2000"
    savedEntity.update

    KeyValue.findById(id) match {
      case Some(g) => {
        assert(g.id == id)
        assert(g.value == "2000")
      }
      case None => assert(false)
    }
  }

  test("Update pertial field") {
    import org.squeryl.PrimitiveTypeMode._

    val savedEntity = new KeyValue("Key-update-partial", "1000").create
    val id = savedEntity.id
    savedEntity.updatePartial(c => c.value := "2000")

    KeyValue.findById(id) match {
      case Some(g) => {
        assert(g.value == "2000")
      }
      case None => assert(false)
    }

  }

}
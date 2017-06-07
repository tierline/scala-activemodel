package com.tierline.scala.activemodel.multitenant.separate

import com.tierline.scala.activemodel.multitenant.MultiTenant
import com.tierline.scala.activemodel.singletenant.domain.{Cart, SeparateTenant}
import org.scalatest._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl._

class SeparateTenantActiveModelTest extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {

  val tenantKey = "tenant-1"

  var tenant: MultiTenant = _

  var session: Session = _

  override def beforeAll {
    //    Database.setSchema(SeparateCentralSchema, SeparateTenantSchema)
    MultiTenant(SeparateTenant)

    session = SessionFactory.newSession
    session.bindToCurrentThread
    transaction {
      SeparateCentralSchema.create()
      tenant = new SeparateTenant(tenantKey).create().multiTenant
      new SeparateTenant("t-2").save()
      assert(SeparateTenant.countAll == 2)
      val tenants = SeparateTenant.all.map { t => t.multiTenant }
      SeparateTenantSchema.create(tenants)
    }
    tenant.scope {
      session = SessionFactory.newSession
      session.bindToCurrentThread
    }

  }

  override def afterAll {
    //    Database.close()
    session.unbindFromCurrentThread
  }

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
      case None => assert(true)
    }
  }

  test("Update") {
    val savedEntity = new Cart("Note", 1000).create()
    val id = savedEntity.id
    savedEntity.name = "new name"
    savedEntity.size = 2000
    savedEntity.update()

    Cart.findById(id) match {
      case Some(g) => {
        assert(g.name == "new name")
        assert(g.size == 2000)
      }
      case None => fail()
    }
  }

  test("Update partial field") {
    import org.squeryl.PrimitiveTypeMode._

    val savedEntity = new Cart("Note", 1000).create()
    val id = savedEntity.id
    savedEntity.updatePartial(c => c.name := "new name")

    Cart.findById(id) match {
      case Some(g) => {
        assert(g.name == "new name")
      }
      case None => fail()
    }

  }

}

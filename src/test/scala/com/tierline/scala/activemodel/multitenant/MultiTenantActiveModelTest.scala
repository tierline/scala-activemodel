package com.tierline.scala.activemodel.multitenant

import org.scalatest._
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

import com.tierline.scala.activemodel.Database
import com.tierline.scala.activemodel.domain._
import com.tierline.sails.servicelocator.logging.Slf4jLogger

class MultiTenantActiveModelTest extends FunSuite with BeforeAndAfterAll with BeforeAndAfter with Slf4jLogger {

  val tenantKey = "tenant-1"

  var tenant: MultiTenant = _

  var session: Session = _

  override def beforeAll {
    Database.setSchema(CentralSchema, MultiTenantSchema)
    MultiTenant(Tenant)

    session = SessionFactory.newSession
    session.bindToCurrentThread
    transaction {
      CentralSchema.create()
      tenant = new Tenant(tenantKey).create().multiTenant
      new Tenant("t-2").save()
      assert(Tenant.countAll == 2)
      val tenants = Tenant.all.map { t => t.multiTenant }
      MultiTenantSchema.create(tenants)
    }
    tenant.scope {
      session = SessionFactory.newSession
      session.bindToCurrentThread
    }

  }

  override def afterAll {
    Database.close()
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
      case Some(g) => assert(false)
      case None => assert(true)
    }
  }

  test("Update") {
    val savedEntity = new Cart("Note", 1000).create()
    val id = savedEntity.id
    savedEntity.name = "new name"
    savedEntity.size = 2000
    savedEntity.update

    Cart.findById(id) match {
      case Some(g) => {
        assert(g.name == "new name")
        assert(g.size == 2000)
      }
      case None => assert(false)
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
      case None => assert(false)
    }

  }

}

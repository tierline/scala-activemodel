package com.tierline.scala.activemodel.multitenant

import org.scalatest._
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._

import com.tierline.scala.activemodel.Database
import com.tierline.scala.activemodel.domain._
import com.tierline.sails.servicelocator.logging.Slf4jLogger

class MultiTenantSchemaTest extends FunSuite with BeforeAndAfterAll with BeforeAndAfter with Slf4jLogger {

  override def beforeAll {
    Database.setSchema(CentralSchema, MultiTenantSchema)
    MultiTenant(Tenant)
  }

  override def afterAll {
    Database.close()
  }

  after {
    transaction {
      val tenants = Tenant.all.map { t => t.multiTenant }
      MultiTenantSchema.drop(tenants)
      CentralSchema.drop()
    }
  }

  test("Create central schema") {
    CentralSchema.newSession.bindToCurrentThread
    transaction {
      CentralSchema.create()
      new Tenant("t-1").save()
      assert(Tenant.countAll == 1)
    }
  }

  test("Create MultiTenant tables on schema") {
    transaction {
      CentralSchema.create()
      new Tenant("t-1").save()
      new Tenant("t-2").save()
      assert(Tenant.countAll == 2)
    }

    transaction {
      val tenants = Tenant.all.map { t => t.multiTenant }
      MultiTenantSchema.create(tenants)
      tenants.foreach { t =>
        t.scope {
          transaction {
            assert(Cart.countAll == 0)
            val c = new Cart().create()
            assert(Cart.countAll == 1)
            new Goods(c.id).save()
            assert(Goods.countAll == 1)
          }
        }
      }
    }
  }

  val defaultTenant = "t-1"

  test("Create Tenant in MultiTenant scope") {
    transaction {
      CentralSchema.create()
      new Tenant(defaultTenant).save()
      new Tenant("t-2").save()
      assert(Tenant.countAll == 2)
    }

    transaction {
      val tenants = Tenant.all.map { t => t.multiTenant }
      MultiTenantSchema.create(tenants)
    }

    val tenant = Tenant.findById(defaultTenant).get
    tenant.scope {
      MultiTenant.outScope {
        new Tenant("t-2").save()
        assert(Tenant.countAll == 3)
      }
      transaction {
        val c = new Cart().create()
        assert(Cart.countAll == 1)
      }
    }

  }

}

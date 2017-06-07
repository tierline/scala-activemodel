package com.tierline.scala.activemodel.multitenant.separate

import com.tierline.scala.activemodel.multitenant.MultiTenant
import com.tierline.scala.activemodel.singletenant.domain.{Cart, Goods, SeparateTenant}
import org.scalatest._
import org.squeryl.PrimitiveTypeMode._

class SeparateSchemaTest extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {
  override def beforeAll {
    //    Database.setSchema(SeparateCentralSchema, SeparateTenantSchema)
    MultiTenant(SeparateTenant)
  }

  override def afterAll {
    //    Database.close()
  }

  after {
    transaction {
      val tenants = SeparateTenant.all.map { t => t.multiTenant }
      SeparateTenantSchema.drop(tenants)
      SeparateCentralSchema.drop()
    }
  }

  test("Create central schema") {
    SeparateCentralSchema.newSession.bindToCurrentThread
    transaction {
      SeparateCentralSchema.create()
      new SeparateTenant("t-1").save()
      assert(SeparateTenant.countAll == 1)
    }
  }

  test("Create MultiTenant tables on schema") {
    transaction {
      SeparateCentralSchema.create()
      new SeparateTenant("t-1").save()
      new SeparateTenant("t-2").save()
      assert(SeparateTenant.countAll == 2)
    }

    transaction {
      val tenants = SeparateTenant.all.map { t => t.multiTenant }
      SeparateTenantSchema.create(tenants)
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
      SeparateCentralSchema.create()
      new SeparateTenant(defaultTenant).save()
      new SeparateTenant("t-2").save()
      assert(SeparateTenant.countAll == 2)
    }

    transaction {
      val tenants = SeparateTenant.all.map { t => t.multiTenant }
      SeparateTenantSchema.create(tenants)
    }

    val tenant = SeparateTenant.findById(defaultTenant).get
    tenant.scope {
      MultiTenant.outScope {
        new SeparateTenant("t-2").save()
        assert(SeparateTenant.countAll == 3)
      }
      transaction {
        val c = new Cart().create()
        assert(Cart.countAll == 1)
      }
    }

  }

}

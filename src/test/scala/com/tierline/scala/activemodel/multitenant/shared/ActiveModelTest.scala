package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.{ActiveModelException, ActiveModelSchema, NativeQuery, TestSuite}
import com.tierline.scala.activemodel.multitenant.domain._
import org.squeryl.PrimitiveTypeMode._

import scala.collection.mutable.ArrayBuffer

trait ActiveModelTest extends TestSuite {

  val sharedSchema = schema

  val numberOfTenant = 5
  val tenants: ArrayBuffer[Tenant] = new ArrayBuffer()

  var tenantName: String = "tierline-"
  var tenantId: String = "T-"

  def addTenantColumns() = {
    NativeQuery(schema).update("ALTER TABLE Channel ADD tenantId VARCHAR(8) not null;")
    NativeQuery(schema).update("ALTER TABLE Comment ADD tenantId VARCHAR(8) not null;")
  }

  override def beforeAll {
    super.beforeAll
    addTenantColumns()
    transaction {
      1 to numberOfTenant foreach { num =>
        this.tenants += new Tenant(tenantId + num, tenantName + num).create()
      }
    }
    assert(tenants.size == numberOfTenant)
  }

  def debugSql(): Unit = {
    schema.setLogger("\n##")
  }

  def mainTenant = tenants.head
}

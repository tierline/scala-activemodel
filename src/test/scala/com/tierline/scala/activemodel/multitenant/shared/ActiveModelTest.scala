package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.MultitenancyException
import com.tierline.scala.activemodel.{ActiveModelException, ActiveModelSchema, NativeQuery, TestSuite}
import com.tierline.scala.activemodel.multitenant.domain._
import com.tierline.scala.activemodel.singletenant.ActiveModelSessionFactory
import org.squeryl.PrimitiveTypeMode._

trait ActiveModelTest extends TestSuite {

  override val schema: ActiveModelSchema = SharedSchema

  val sharedSchema = SharedSchema

  var mainTenant: Tenant = _

  var tenantName: String = "tierline"
  var tenantId: String = "C-101"

  def addTenantColumns() = {
    NativeQuery(schema).update("ALTER TABLE Channel ADD tenantId VARCHAR(8) not null;")
  }

  override def beforeAll {
    super.beforeAll
    addTenantColumns()
    transaction {
      this.mainTenant = new Tenant(tenantId, tenantName).create()
    }
  }

  def debugSql(): Unit = {
    schema.setLogger("##")
  }

}

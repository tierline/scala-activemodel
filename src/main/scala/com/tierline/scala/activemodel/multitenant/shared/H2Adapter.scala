package com.tierline.scala.activemodel.multitenant.shared

import java.sql.PreparedStatement

import org.squeryl.{Session, Table}
import org.squeryl.internals.StatementWriter

import scala.collection.mutable.ListBuffer


class H2Adapter extends org.squeryl.adapters.H2Adapter {

  override def writeInsert[T](obj: T, table: Table[T], s: StatementWriter): Unit = {
    if (Multitenancy.hook) {
      val obj_ = obj.asInstanceOf[AnyRef]
      val fields = getInsertableFields(table.posoMetaData.fieldsMetaData)
      s.write("insert into ")
      s.write(quoteName(table.prefixedName))
      s.write(" (")
      val fieldNames = fields.map(fmd => quoteName(fmd.columnName)).mkString(", ")
      s.write(s"$fieldNames, tenantId")
      s.write(") values (")
      val values = fields.map(fmd => writeValue(obj_, fmd, s)).mkString(", ")
      s.write(s"$values, ?)")
    } else {
      super.writeInsert(obj, table, s)
    }

  }

  override def executeUpdateForInsert(s: Session, sw: StatementWriter, ps: PreparedStatement) = exec(s, sw) { params =>
    if (Multitenancy.hook(sw)) {
      val tenantId = Multitenancy.currentTenant.value

      val buffer = new ListBuffer[AnyRef]()
      params.foreach(p => buffer += p)
      val tenantParams = buffer += tenantId

      if (s.isLoggingEnabled)
        s.log(sw.toString)
      fillParamsInto(tenantParams, ps)
      ps.executeUpdate
    } else {
      super.executeUpdateForInsert(s, sw, ps)
    }
  }
}

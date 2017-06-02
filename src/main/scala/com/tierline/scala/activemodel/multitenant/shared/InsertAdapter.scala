package com.tierline.scala.activemodel.multitenant.shared

import java.sql.PreparedStatement

import com.tierline.scala.activemodel.multitenant.MultitenancyException
import org.squeryl.{Session, Table}
import org.squeryl.internals.{DatabaseAdapter, StatementWriter}


trait InsertAdapter extends DatabaseAdapter {
  self: SharedDatabaseAdapter =>

  override def writeInsert[T](obj: T, table: Table[T], s: StatementWriter): Unit = {
    if (!Multitenancy.hook) {
      super.writeInsert(obj, table, s)
    } else {
      val obj_ = obj.asInstanceOf[AnyRef]
      val fields = getInsertableFields(table.posoMetaData.fieldsMetaData)
      s.write("insert into ")
      s.write(quoteName(table.prefixedName))
      s.write(" (")
      val fieldNames = fields.map(fmd => quoteName(fmd.columnName)).mkString(", ")
      s.write(s"$fieldNames, $tenantIdColumnName")
      s.write(") values (")
      val values = fields.map(fmd => writeValue(obj_, fmd, s)).mkString(", ")
      s.write(s"$values, ?)")
    }
  }

  override def executeUpdateForInsert(s: Session, sw: StatementWriter, ps: PreparedStatement) = exec(s, sw) { params =>
    if (!Multitenancy.hook(sw)) {
      super.executeUpdateForInsert(s, sw, ps)
    } else {
      Multitenancy.currentTenantValue match {
        case Some(tenantId) =>
          if (s.isLoggingEnabled) s.log(sw.toString)
          fillParamsInto(addTailTenantParams(tenantId, params), ps)
          ps.executeUpdate
        case None =>
          throw new MultitenancyException()
      }
    }
  }
}

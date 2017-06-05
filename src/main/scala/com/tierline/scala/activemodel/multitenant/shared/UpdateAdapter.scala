package com.tierline.scala.activemodel.multitenant.shared

import com.tierline.scala.activemodel.multitenant.MultitenancyException
import org.squeryl.{Session, Table}
import org.squeryl.internals.{DatabaseAdapter, StatementWriter}


trait UpdateAdapter extends DatabaseAdapter {
  self: SharedDatabaseAdapter =>


  override def writeUpdate[T](o: T, t: Table[T], sw: StatementWriter, checkOCC: Boolean) = {
    if (!Multitenancy.hook) {
      super.writeUpdate(o, t, sw, checkOCC)
    } else {

      val o_ = o.asInstanceOf[AnyRef]
      val tableName = quoteName(t.prefixedName)

      sw.write("update ", tableName, " set ")
      sw.nextLine
      sw.indent
      sw.writeLinesWithSeparator(
        t.posoMetaData.fieldsMetaData.
          filter(fmd => !fmd.isIdFieldOfKeyedEntity && fmd.isUpdatable).
          map(fmd => {
            if (fmd.isOptimisticCounter)
              quoteName(fmd.columnName) + " = " + quoteName(fmd.columnName) + " + 1 "
            else
              quoteName(fmd.columnName) + " = " + writeValue(o_, fmd, sw)
          }),
        ","
      )
      sw.unindent
      sw.write("where")
      sw.nextLine
      sw.indent

      t.posoMetaData.primaryKey match {
        case Some(pk) =>
          pk.fold(
            pkMd => sw.write(quoteName(pkMd.columnName), " = ", writeValue(o_, pkMd, sw)),
            pkGetter => throw new UnsupportedOperationException("not implemented multi key"))
        case None => org.squeryl.internals.Utils.throwError("not pk")
      }

      sw.nextLine
      sw.write(s" and ($tableName.$tenantIdColumnName = ?)")
      sw.nextLine

      if (checkOCC)
        t.posoMetaData.optimisticCounter.foreach(occ => {
          sw.write(" and ")
          sw.write(quoteName(occ.columnName))
          sw.write(" = ")
          sw.write(writeValue(o_, occ, sw))
        })
    }
  }


  override def executeUpdateAndCloseStatement(s: Session, sw: StatementWriter): Int = exec(s, sw) { params =>
    if (!Multitenancy.hook(sw)) {
      super.executeUpdateAndCloseStatement(s, sw)
    } else {
      Multitenancy.currentTenantValue match {
        case Some(tenantId) =>
          if (s.isLoggingEnabled) s.log(sw.toString)

          val st = prepareStatement(s.connection, sw.statement)
          fillParamsInto(addTailTenantParams(tenantId, params), st)
          try {
            st.executeUpdate
          }
          finally {
            st.close()
          }
        case None =>
          throw new MultitenancyException()
      }
    }
  }


}

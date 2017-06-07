package com.tierline.scala.activemodel.multitenant.shared

import org.squeryl.dsl.ast.ExpressionNode
import org.squeryl.internals.{DatabaseAdapter, StatementWriter}
import org.squeryl.{Session, Table}


trait DeleteAdapter extends DatabaseAdapter {
  self: SharedDatabaseAdapter =>

  override def writeDelete[T](t: Table[T], whereClause: Option[ExpressionNode], sw: StatementWriter) = {
    if (!Multitenancy.hook) {
      super.writeDelete(t, whereClause, sw)
    } else {
      val tableName = quoteName(t.prefixedName)
      sw.write("delete from ")
      sw.write(tableName)

      whereClause match {
        case Some(w) =>
          sw.nextLine
          sw.write("where")
          sw.writeIndented {
            w.write(sw)
          }
          sw.nextLine
          sw.write(s"and ($tableName.$tenantIdColumnName = ?)")
          sw.nextLine
        case None =>
          sw.nextLine
          sw.write(s"where ($tableName.$tenantIdColumnName = ?)")
      }
    }
  }

}

package com.tierline.scala.activemodel.multitenant.shared

import java.sql.PreparedStatement

import com.tierline.scala.activemodel.multitenant.MultitenancyException
import org.squeryl.dsl.CompositeKey
import org.squeryl.dsl.ast.{EqualityExpression, ExpressionNode, InputOnlyConstantExpressionNode, UpdateStatement}
import org.squeryl.{Session, Table}
import org.squeryl.internals.{StatementWriter, Utils}

import scala.collection.mutable.ListBuffer


class H2Adapter extends org.squeryl.adapters.H2Adapter {

  def tenantIdColumnName = "tenantId"

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

          fillParamsInto(createTenantParams(tenantId, params), ps)
          ps.executeUpdate

        case None =>
          throw new MultitenancyException()
      }
    }
  }

  protected def createTenantParams(tenantId: String, params: Iterable[AnyRef]): Iterable[AnyRef] = {
    val buffer = new ListBuffer[AnyRef]()
    params.foreach(p => buffer += p)
    buffer += tenantId
    buffer
  }

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
          sw.nextLine
          sw.writeIndented {
            w.write(sw)
          }
          sw.nextLine
          sw.write(s" and ($tableName.$tenantIdColumnName = ?)")
        case None =>
          sw.nextLine
          sw.write(s"where ($tableName.$tenantIdColumnName = ?)")
      }
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
          fillParamsInto(createTenantParams(tenantId, params), st)
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


  override def writeUpdate[T](o: T, t: Table[T], sw: StatementWriter, checkOCC: Boolean) = {
    if (!Multitenancy.hook) {
      super.writeUpdate(o, t, sw, checkOCC)
    } else {

      val o_ = o.asInstanceOf[AnyRef]

      sw.write("update ", quoteName(t.prefixedName), " set ")
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
      sw.write(s"and ($tenantIdColumnName = ?)")

      if (checkOCC)
        t.posoMetaData.optimisticCounter.foreach(occ => {
          sw.write(" and ")
          sw.write(quoteName(occ.columnName))
          sw.write(" = ")
          sw.write(writeValue(o_, occ, sw))
        })
    }
  }

}
